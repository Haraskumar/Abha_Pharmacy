# Abha Pharmacy Management System

A comprehensive, full-stack web application for managing a modern retail pharmacy. The proposed system is a dual-sided web application comprising a pharmacist/admin portal for inventory, sales, alerts, and fulfillment, and a customer portal for medicine discovery, prescription submission, order/refill requests, and delivery/pickup workflows. 

## Technology Stack
- **Backend:** Java, Spring Boot
- **Frontend:** HTML5, CSS, Thymeleaf (Server-Side Rendering)
- **Database:** MySQL (Accessed via raw JdbcTemplate & PreparedStatement)
- **Build Tool:** Maven

---

## Core Features & Workflows

### 1. Customer Authentication and Profiles
- **Dual Authentication:** Separate login paths/authorization for customers and staff, with role-based access control. Passwords stored only as secure hashes.
- **Registration:** Customers can create an account and maintain complete demographic and address data.

### 2. Catalog Search & Medicine Discovery
- **Searchable Catalog:** Customers can search medicines by name and view customer-visible availability and prices without seeing sensitive staff fields like cost prices.
- **Availability:** Reflects current real-time batch stock state. 

### 3. Inventory & Batch Management
- **Medicine Master Management:** Staff add and update medicine catalog data (Name, Manufacturer, Category, OTC vs Prescription logic, thresholds).
- **Strict Batch Tracking:** Medicines are tracked at the batch level. Each batch has its own Manufacturing Date, Expiry Date, Quantity In Stock, Cost Price, Selling Price, Rack Location, and Supplier linkage.
- **Auto-Aggregation:** If new stock arrives for an existing batch, the system automatically aggregates it by adding the new quantity to the existing row, preventing database clutter while updating the total purchase cost.

### 4. Alert Management (Stock & Expiry)
- **Low-Stock Alerts:** The system detects when inventory falls below the configured Reorder Threshold and generates an alert.
- **Near-Expiry Alerts:** Medicines approaching expiry (e.g., within a 30-day window) trigger warnings on the dashboard.
- **Alert Resolution:** Staff resolve alerts, recording the action taken and the resolution date/time for operational traceability.

### 5. Point of Sale (POS) - In-Store Checkout
- **Walk-in Sales:** Pharmacists select items, batches, and quantities to process in-store checkouts.
- **Stock Deduction:** Completing a POS sale securely deducts stock atomically to prevent negative inventory.
- **Digital Receipts:** System provides a receipt view with order identifiers, date/time, item quantities, sale prices, and discount amounts.

### 6. Prescription Upload & Verification
- **Image Upload:** Registered customers can upload prescription images (JPG/PNG) along with instructions. The files are securely protected and not publicly addressable without authorization.
- **Pharmacist Review:** Uploaded prescriptions appear in the staff portal queue. Staff can verify or reject them and record remarks. 
- **Auditability:** Verification captures the responsible staff member and timestamp.

### 7. Online Orders & Fulfillment
- **Pickup & Home Delivery:** Customers can request pickup or home delivery. Delivery orders automatically persist address fields.
- **Refill Requests:** Customers can request refills using their prior purchase/order history, seamlessly creating a new linked request.
- **Order Fulfillment Dashboard:** Staff view, update, and fulfill online orders. Customers can view their order history and track fulfillment statuses.

### 8. Revenue & Reporting
- **Business Reporting:** Staff can view daily, weekly, and monthly revenue summaries generated dynamically from recorded, completed sales transactions.

### 9. Supplier Management
- **Vendor Tracking:** Staff maintain supplier records for procurement, including GSTIN and contact info. Batch records are tied to these suppliers for supply-chain traceability.

---

## Database Architecture & Entity Attributes

The system uses a highly normalized relational schema designed for strict data integrity based on the ERD and Relational Schema guidelines. Below are all the specific entities and their precise attributes (without hypothesis):

### 1. CUSTOMER
Stores registered online buyers and their profiles/addresses.
- `CustomerID` (Primary Key)
- `FirstName`
- `LastName`
- `HouseNo`
- `Street`
- `City`
- `Pincode`
- `Email`
- `PasswordHash`
- `DateOfBirth`
- `Gender`
- `RegistrationDate`

### 2. CUSTOMER_PHONE
Stores one-to-many phone numbers for customers.
- `CustomerID` (Foreign Key)
- `PhoneNumber` (Primary Key part)

### 3. STAFF
Stores Pharmacists and Owners (staff identity, employment, and authorization).
- `StaffID` (Primary Key)
- `FirstName`
- `LastName`
- `Email`
- `PasswordHash`
- `Role`
- `LicenseNumber`
- `HireDate`
- `Salary`

### 4. STAFF_PHONE
Stores one-to-many phone numbers for staff members.
- `StaffID` (Foreign Key)
- `PhoneNumber` (Primary Key part)

### 5. SUPPLIER
Vendor details for inventory sourcing.
- `SupplierID` (Primary Key)
- `SupplierName`
- `ContactPerson`
- `Email`
- `GSTIN`
- `Street`
- `City`
- `State`
- `Pincode`

### 6. SUPPLIER_PHONE
Stores one-to-many phone numbers for suppliers.
- `SupplierID` (Foreign Key)
- `PhoneNumber` (Primary Key part)

### 7. MEDICINE
Base catalog of drugs and inventory threshold rules.
- `MedicineID` (Primary Key)
- `MedicineName`
- `GenericName`
- `Manufacturer`
- `Category`
- `RequiresPrescription`
- `ReorderThreshold`
- `BaseUnitPrice`
- `StorageCondition`
- `Description`

### 8. BATCH
Physical stock records linked to specific medicines.
- `MedicineID` (Primary Key/Foreign Key)
- `BatchNo` (Primary Key)
- `MfgDate`
- `ExpiryDate`
- `QuantityInStock`
- `CostPrice`
- `SellingPrice`
- `RackLocation`
- `SupplierID` (Foreign Key)
- `SupplyDate`
- `PurchaseAmount`

### 9. PRESCRIPTION
Customer uploads awaiting verification.
- `PrescriptionID` (Primary Key)
- `PrescriptionImageURL`
- `UploadDate`
- `UploadTime`
- `DoctorName`
- `PatientName`
- `VerificationStatus`
- `PharmacistRemarks`
- `Instructions`
- `CustomerID` (Foreign Key)
- `StaffID` (Foreign Key)
- `VerificationDate`
- `VerificationTime`

### 10. ORDER
Unified table for POS walk-in sales, pickups, deliveries, and refills.
- `OrderID` (Primary Key)
- `OrderDate`
- `OrderTime`
- `OrderType`
- `OrderStatus`
- `PaymentMethod`
- `PaymentStatus`
- `DiscountAmount`
- `HouseNo` (for delivery)
- `Street` (for delivery)
- `City` (for delivery)
- `Pincode` (for delivery)
- `CustomerID` (Foreign Key)
- `StaffID` (Foreign Key)
- `PrescriptionID` (Foreign Key)

### 11. ORDER_ITEM
Line items for an order, linking sold quantities strictly to specific batches.
- `OrderID` (Primary Key/Foreign Key)
- `ItemSeqNo` (Primary Key)
- `Quantity`
- `UnitPriceAtSale`
- `MedicineID` (Foreign Key)
- `BatchNo` (Foreign Key)

### 12. ALERT
System-generated notifications tracking exceptions (e.g. stock, expiry) and their resolutions.
- `AlertID` (Primary Key)
- `AlertType`
- `Message`
- `Status`
- `CreatedAt`
- `ResolvedAt`
- `MedicineID` (Foreign Key)
- `BatchNo` (Foreign Key)
- `StaffID` (Foreign Key)
- `ResolvedDate`
- `ActionTaken`

---

## How to Build and Run the Application

### Prerequisites
1. Java 17+ installed.
2. Maven installed.
3. MySQL Server running locally on port `3306`.

### Setup Instructions
1. **Configure Database:**
   Create a database named `abha_pharmacy` in your MySQL instance.
   ```sql
   CREATE DATABASE abha_pharmacy;
   ```

2. **Update Database Credentials:**
   Open `src/main/resources/application.properties` and ensure the MySQL username and password match your local setup:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/abha_pharmacy
   spring.datasource.username=root
   spring.datasource.password=your_password
   ```

3. **Database Initialization Mode:**
   To cleanly build the database schema and insert dummy data, ensure this line is active in `application.properties`:
   ```properties
   spring.sql.init.mode=always
   ```
   *(Note: Once the app runs successfully for the first time, change this back to `never` so your data isn't wiped on the next restart).*

4. **Run the Application:**
   Open a terminal in the project root directory and run:
   ```bash
   mvn clean spring-boot:run
   ```

5. **Access the App:**
   - **Customer Portal:** `http://localhost:8081/`
   - **Admin/Staff Portal:** `http://localhost:8081/login/admin`
   - *Default Owner Login:* `owner@abhapharmacy.com` / `password123`

---

## Code Base Structure & Rules
- **No ORM Frameworks:** The project strictly avoids Spring Data JPA / Hibernate. All database interactions are written in pure SQL strings using `JdbcTemplate`, `RowMapper`, and `PreparedStatement`.
- **Server-Side UI:** UI is rendered exclusively via Thymeleaf templates (`.html` files in `src/main/resources/templates/`), relying on standard form submissions rather than REST APIs.
- **No Comments Rule:** Code is written cleanly and self-documenting without explanatory comments in the Java, HTML, CSS, or SQL files.
