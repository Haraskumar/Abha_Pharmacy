# Abha Pharmacy — Comprehensive Database Presentation & Viva Master Guide
**Course**: CSE302: Database Management Systems (IIT BHU)  
**Project**: Abha Pharmacy Digital Transformation Platform  
**Architecture**: Multi-Tier MVC with Pure JDBC Persistence (No JPA/Hibernate ORM Abstraction)  
**RDBMS**: MySQL 8.0 (InnoDB Storage Engine)

---

## Quick Reference Table for Evaluator Questions

| Concept | Implementation in Abha Pharmacy | Code / SQL Reference |
| :--- | :--- | :--- |
| **Data Persistence** | Pure JDBC via Spring `JdbcTemplate` | `*Repository.java` (Zero Hibernate black box) |
| **1NF Normalization** | Multi-valued phone numbers decomposed | `CUSTOMER_PHONE`, `STAFF_PHONE`, `SUPPLIER_PHONE` |
| **2NF Normalization** | Composite key dependencies resolved | `ORDER_ITEM(OrderID, ItemSeqNo)`, `BATCH(MedicineID, BatchNo)` |
| **3NF Normalization** | Transitive dependencies eliminated | Drug names in `MEDICINE`, supplier info in `SUPPLIER` |
| **Referential Integrity** | `ON DELETE CASCADE` & `ON DELETE SET NULL` | `schema.sql` foreign key definitions |
| **ACID: Atomicity** | Spring `@Transactional` rollbacks | `OrderService.placeCartOrder`, `processPosSale` |
| **ACID: Isolation** | Atomic condition update `QuantityInStock >= ?` | `BatchRepository.deductStock()` |
| **Concurrency / Upsert** | `ON DUPLICATE KEY UPDATE` | `BatchRepository.upsertBatch()` |
| **Multi-Table JOINs** | 6-table inner & left outer joins with `COALESCE` | `OrderRepository.findScheduleHDispensations()` |
| **Aggregations & Analytics** | `SUM`, `COUNT(DISTINCT)`, `GROUP BY`, Profit Math | `OrderRepository.getRevenueAnalytics()` |
| **Subqueries** | Correlated subquery for latest selling price | `MedicineRepository.findAll()` |
| **Conditional Aggregation** | `SUM(CASE WHEN ExpiryDate >= CURRENT_DATE...)` | `MedicineRepository.findAll()` |
| **Database Views** | Virtual tables for security & query abstraction | `v_active_inventory`, `v_schedule_h_register` |
| **Database Indexes** | Composite B-Tree indexes for fast FEFO lookups | `idx_batch_expiry`, `idx_order_date_status` |
| **Regulatory FEFO** | First-Expiry-First-Out dispensing logic | `OrderService.java` & SQL `ORDER BY ExpiryDate ASC` |

---

## 1. DDL, ALTER, and Schema Evolution

### Schema Alteration (Handling Multi-Valued Attributes)
When supporting multiple phone numbers per customer or supplier, storing comma-separated strings violates **First Normal Form (1NF)**. Instead, we decompose multi-valued attributes into independent tables with composite primary keys:

```sql
CREATE TABLE CUSTOMER_PHONE (
    CustomerID INT,
    PhoneNumber VARCHAR(15),
    PRIMARY KEY (CustomerID, PhoneNumber),
    FOREIGN KEY (CustomerID) REFERENCES CUSTOMER(CustomerID) ON DELETE CASCADE
);
```

### Foreign Key Constraints & Referential Integrity
- **`ON DELETE CASCADE`**: When a customer is deleted, all associated phone records in `CUSTOMER_PHONE` are automatically deleted to prevent orphaned records.
- **Composite Foreign Key**: `ORDER_ITEM` references the composite primary key `(MedicineID, BatchNo)` of `BATCH`:
```sql
FOREIGN KEY (MedicineID, BatchNo) REFERENCES BATCH(MedicineID, BatchNo)
```

---

## 2. Normalization Breakdown (1NF, 2NF, 3NF, BCNF)

### 1NF (Atomicity & Multi-Valued Attributes)
- Every column holds atomic, indivisible values.
- Phone numbers are isolated into `CUSTOMER_PHONE`, `STAFF_PHONE`, and `SUPPLIER_PHONE`.
- Address fields (`HouseNo`, `Street`, `City`, `Pincode`) are decomposed instead of stored as a single unstructured text block.

### 2NF (No Partial Key Dependencies)
- In tables with composite primary keys, every non-key attribute must functionally depend on the entire composite key.
- Example: `ORDER_ITEM (OrderID, ItemSeqNo, Quantity, UnitPriceAtSale, MedicineID, BatchNo)`
  - Primary Key: `(OrderID, ItemSeqNo)`
  - `Quantity` and `UnitPriceAtSale` depend on the specific sequence item of that specific order.
  - `OrderDate` is kept in `ORDER`, not in `ORDER_ITEM`, because it depends solely on `OrderID`.

### 3NF (No Transitive Dependencies)
- Non-key columns must not depend on other non-key columns ($X \to Y$ where neither is a candidate key).
- In `ORDER_ITEM`, we store only `MedicineID` (foreign key) and NOT `MedicineName`, `Category`, or `Manufacturer`. To display medicine details, the application executes a relational `JOIN`:
```sql
SELECT oi.OrderID, oi.Quantity, m.MedicineName, m.Category 
FROM ORDER_ITEM oi
JOIN MEDICINE m ON oi.MedicineID = m.MedicineID
WHERE oi.OrderID = ?;
```

---

## 3. ACID Properties in Action

### 1. Atomicity (All-or-Nothing Execution)
Implemented via Spring's declarative `@Transactional` boundaries:
```java
@Transactional
public void placeCartOrder(Integer customerId, Map<Integer, Integer> cartItems) {
    // 1. Insert parent row in `ORDER`
    // 2. Insert rows into `ORDER_ITEM` for each cart line item
    // 3. Atomically decrement stock in `BATCH` via deductStock()
    // If Step 3 fails for ANY item (insufficient stock), steps 1 and 2 roll back!
}
```

### 2. Consistency (State Validation & Integrity Checks)
- Database-level constraints (`NOT NULL`, `CHECK`, foreign keys).
- Application-level pre-conditions:
  - Expiry dates must be strictly after manufacturing dates.
  - Inward batches cannot have expired dates.
  - Retail OTC orders are capped at 10 units to deter unauthorized resale.

### 3. Isolation & Race-Condition Prevention
When two concurrent users attempt to purchase the last unit of medication:
```sql
-- BatchRepository.java
UPDATE BATCH 
SET QuantityInStock = QuantityInStock - ? 
WHERE MedicineID = ? AND BatchNo = ? AND QuantityInStock >= ?;
```
*Why this matters:* The clause `AND QuantityInStock >= ?` ensures MySQL's row-level lock evaluates the condition atomically at write time. If another transaction deducted the stock milliseconds earlier, the updated row count returns `0`, triggering an immediate rollback.

### 4. Durability
MySQL's InnoDB engine writes transaction logs to the Write-Ahead Log (WAL / `ib_logfile`) before committing. When Tomcat or the server restarts, all committed orders are fully persisted.

---

## 4. Complex SQL Queries & Aggregations

### 1. 6-Table Relational JOIN (Schedule H Statutory Audit Register)
```sql
SELECT 
    o.OrderID, o.OrderDate, o.OrderTime,
    COALESCE(p.PatientName, CONCAT(c.FirstName, ' ', c.LastName), 'Walk-in Patient') AS PatientName,
    COALESCE(CONCAT(o.HouseNo, ', ', o.Street, ', ', o.City, ' - ', o.Pincode), 'Dispensary Counter Walk-in') AS PatientAddress,
    COALESCE(p.DoctorName, 'Dr. R. K. Singhal, MD') AS DoctorName,
    m.MedicineName, m.GenericName, m.Category, m.Manufacturer,
    oi.BatchNo, oi.Quantity, oi.UnitPriceAtSale,
    COALESCE(s.FirstName, 'Pharm. Rajesh') AS PharmacistName,
    COALESCE(s.LicenseNumber, 'LIC-PH-2024') AS PharmacistLicense
FROM `ORDER` o
JOIN ORDER_ITEM oi ON o.OrderID = oi.OrderID
JOIN MEDICINE m ON oi.MedicineID = m.MedicineID
LEFT JOIN PRESCRIPTION p ON o.PrescriptionID = p.PrescriptionID
LEFT JOIN CUSTOMER c ON o.CustomerID = c.CustomerID
LEFT JOIN STAFF s ON COALESCE(o.StaffID, p.StaffID) = s.StaffID
WHERE m.RequiresPrescription = 1 OR o.PrescriptionID IS NOT NULL
ORDER BY o.OrderDate DESC, o.OrderTime DESC;
```

### 2. Correlated Subquery & Conditional Aggregation (Real-Time Stock & Pricing)
```sql
SELECT 
    m.*, 
    COALESCE(SUM(CASE WHEN b.ExpiryDate >= CURRENT_DATE THEN b.QuantityInStock ELSE 0 END), 0) AS TotalStock, 
    COALESCE(SUM(b.QuantityInStock), 0) AS GrossStock, 
    (SELECT SellingPrice FROM BATCH 
     WHERE MedicineID = m.MedicineID 
     ORDER BY SupplyDate DESC, ExpiryDate DESC LIMIT 1) AS LatestPrice 
FROM MEDICINE m 
LEFT JOIN BATCH b ON m.MedicineID = b.MedicineID 
GROUP BY m.MedicineID;
```

### 3. Financial Analytics & Margin Computation
```sql
SELECT 
    COALESCE(SUM(oi.Quantity * oi.UnitPriceAtSale) - COALESCE(SUM(DISTINCT o.DiscountAmount), 0), 0) AS totalRevenue,
    COUNT(DISTINCT o.OrderID) AS completedOrders,
    COALESCE(SUM(oi.Quantity * (oi.UnitPriceAtSale - COALESCE(b.CostPrice, oi.UnitPriceAtSale * 0.70))), 0) AS totalProfit
FROM `ORDER` o
LEFT JOIN ORDER_ITEM oi ON o.OrderID = oi.OrderID
LEFT JOIN BATCH b ON oi.MedicineID = b.MedicineID AND oi.BatchNo = b.BatchNo
WHERE o.OrderStatus = 'COMPLETED' OR o.PaymentStatus = 'PAID';
```

---

## 5. Database Views (`CREATE VIEW`)

### View 1: Real-Time Active Inventory (`v_active_inventory`)
```sql
CREATE OR REPLACE VIEW v_active_inventory AS
SELECT 
    m.MedicineID,
    m.MedicineName,
    m.GenericName,
    m.Category,
    m.RequiresPrescription,
    m.ReorderThreshold,
    COALESCE(SUM(CASE WHEN b.ExpiryDate >= CURRENT_DATE THEN b.QuantityInStock ELSE 0 END), 0) AS TotalActiveStock,
    COALESCE(SUM(CASE WHEN b.ExpiryDate < CURRENT_DATE THEN b.QuantityInStock ELSE 0 END), 0) AS ExpiredStock,
    MIN(CASE WHEN b.ExpiryDate >= CURRENT_DATE AND b.QuantityInStock > 0 THEN b.SellingPrice ELSE NULL END) AS MinSellingPrice
FROM MEDICINE m
LEFT JOIN BATCH b ON m.MedicineID = b.MedicineID
GROUP BY m.MedicineID, m.MedicineName, m.GenericName, m.Category, m.RequiresPrescription, m.ReorderThreshold;
```

### View 2: Regulatory Schedule H Register (`v_schedule_h_register`)
```sql
CREATE OR REPLACE VIEW v_schedule_h_register AS
SELECT 
    o.OrderID,
    o.OrderDate,
    o.OrderTime,
    COALESCE(p.PatientName, CONCAT(c.FirstName, ' ', c.LastName), 'Walk-in Patient') AS PatientName,
    COALESCE(p.DoctorName, 'Dr. R. K. Singhal, MD') AS DoctorName,
    m.MedicineName,
    m.GenericName,
    oi.BatchNo,
    oi.Quantity,
    oi.UnitPriceAtSale,
    s.FirstName AS DispensedByStaff,
    s.LicenseNumber AS PharmacistLicense
FROM `ORDER` o
JOIN ORDER_ITEM oi ON o.OrderID = oi.OrderID
JOIN MEDICINE m ON oi.MedicineID = m.MedicineID
LEFT JOIN PRESCRIPTION p ON o.PrescriptionID = p.PrescriptionID
LEFT JOIN CUSTOMER c ON o.CustomerID = c.CustomerID
LEFT JOIN STAFF s ON COALESCE(o.StaffID, p.StaffID) = s.StaffID
WHERE m.RequiresPrescription = 1 OR o.PrescriptionID IS NOT NULL;
```

---

## 6. Database Triggers (Automated In-DB Integrity)

You can demonstrate this trigger during viva to show automated database-level business logic:

```sql
DELIMITER $$

CREATE TRIGGER trg_check_batch_dates
BEFORE INSERT ON BATCH
FOR EACH ROW
BEGIN
    IF NEW.ExpiryDate <= NEW.MfgDate THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Integrity Violation: Expiry date must be strictly after manufacturing date.';
    END IF;
    IF NEW.ExpiryDate < CURRENT_DATE() THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Integrity Violation: Cannot inward an already expired batch into active inventory.';
    END IF;
END$$

DELIMITER ;
```

---

## 7. Stored Procedure: First-Expiry-First-Out (FEFO) Allocation

```sql
DELIMITER $$

CREATE PROCEDURE sp_allocate_fefo_batch(
    IN in_medicine_id INT,
    IN in_required_qty INT,
    OUT out_batch_no VARCHAR(50),
    OUT out_unit_price DECIMAL(10,2),
    OUT out_status VARCHAR(50)
)
BEGIN
    DECLARE v_batch_no VARCHAR(50);
    DECLARE v_price DECIMAL(10,2);

    SELECT BatchNo, SellingPrice INTO v_batch_no, v_price
    FROM BATCH
    WHERE MedicineID = in_medicine_id 
      AND QuantityInStock >= in_required_qty 
      AND ExpiryDate > CURRENT_DATE()
    ORDER BY ExpiryDate ASC
    LIMIT 1;

    IF v_batch_no IS NOT NULL THEN
        SET out_batch_no = v_batch_no;
        SET out_unit_price = v_price;
        SET out_status = 'ALLOCATED';
    ELSE
        SET out_batch_no = NULL;
        SET out_unit_price = 0.00;
        SET out_status = 'INSUFFICIENT_STOCK';
    END IF;
END$$

DELIMITER ;
```

---

## 8. Query Optimization via B-Tree Indexes

```sql
-- 1. Accelerates FEFO batch lookups by medicine & ascending expiry
CREATE INDEX idx_batch_expiry ON BATCH (MedicineID, ExpiryDate);

-- 2. Optimizes date-range order lookups and status filtering
CREATE INDEX idx_order_date_status ON `ORDER` (OrderDate, OrderStatus);

-- 3. Speeds up customer order history queries
CREATE INDEX idx_order_customer ON `ORDER` (CustomerID);

-- 4. Accelerates order line item joins
CREATE INDEX idx_orderitem_order ON ORDER_ITEM (OrderID);

-- 5. Accelerates active alert filtering
CREATE INDEX idx_alert_status_med ON ALERT (Status, MedicineID);
```
