DROP TABLE IF EXISTS ALERT;
DROP TABLE IF EXISTS ORDER_ITEM;
DROP TABLE IF EXISTS `ORDER`;
DROP TABLE IF EXISTS PRESCRIPTION;
DROP TABLE IF EXISTS BATCH;
DROP TABLE IF EXISTS MEDICINE;
DROP TABLE IF EXISTS SUPPLIER_PHONE;
DROP TABLE IF EXISTS SUPPLIER;
DROP TABLE IF EXISTS STAFF_PHONE;
DROP TABLE IF EXISTS STAFF;
DROP TABLE IF EXISTS CUSTOMER_PHONE;
DROP TABLE IF EXISTS CUSTOMER;

CREATE TABLE CUSTOMER ( 
    CustomerID INT AUTO_INCREMENT PRIMARY KEY,
    FirstName VARCHAR(50),
    LastName VARCHAR(50),
    HouseNo VARCHAR(50),
    Street VARCHAR(100),
    City VARCHAR(50),
    Pincode VARCHAR(10),
    Email VARCHAR(100) UNIQUE,
    PasswordHash VARCHAR(255),
    DateOfBirth DATE,
    Gender VARCHAR(10),
    RegistrationDate DATE
);

CREATE TABLE CUSTOMER_PHONE (
    CustomerID INT,
    PhoneNumber VARCHAR(15),
    PRIMARY KEY (CustomerID, PhoneNumber),
    FOREIGN KEY (CustomerID) REFERENCES CUSTOMER(CustomerID) ON DELETE CASCADE
);

CREATE TABLE STAFF (
    StaffID INT AUTO_INCREMENT PRIMARY KEY,
    FirstName VARCHAR(50),
    LastName VARCHAR(50),
    Email VARCHAR(100) UNIQUE,
    PasswordHash VARCHAR(255),
    Role VARCHAR(30),
    LicenseNumber VARCHAR(50),
    HireDate DATE,
    Salary DECIMAL(10,2)
);

CREATE TABLE STAFF_PHONE (
    StaffID INT,
    PhoneNumber VARCHAR(15),
    PRIMARY KEY (StaffID, PhoneNumber),
    FOREIGN KEY (StaffID) REFERENCES STAFF(StaffID) ON DELETE CASCADE
);

CREATE TABLE SUPPLIER (
    SupplierID INT AUTO_INCREMENT PRIMARY KEY,
    SupplierName VARCHAR(100),
    ContactPerson VARCHAR(100),
    Email VARCHAR(100),
    GSTIN VARCHAR(20),
    Street VARCHAR(100),
    City VARCHAR(50),
    State VARCHAR(50),
    Pincode VARCHAR(10)
);

CREATE TABLE SUPPLIER_PHONE (
    SupplierID INT,
    PhoneNumber VARCHAR(15),
    PRIMARY KEY (SupplierID, PhoneNumber),
    FOREIGN KEY (SupplierID) REFERENCES SUPPLIER(SupplierID) ON DELETE CASCADE
);

CREATE TABLE MEDICINE (
    MedicineID INT AUTO_INCREMENT PRIMARY KEY,
    MedicineName VARCHAR(100) NOT NULL,
    GenericName VARCHAR(100) NOT NULL,
    Manufacturer VARCHAR(100) NOT NULL,
    Category VARCHAR(50) NOT NULL,
    RequiresPrescription BOOLEAN NOT NULL,
    ReorderThreshold INT NOT NULL,
    StorageCondition VARCHAR(100) NOT NULL,
    Description TEXT NOT NULL
);

CREATE TABLE BATCH (
    MedicineID INT,
    BatchNo VARCHAR(50),
    RackLocation VARCHAR(50),
    MfgDate DATE NOT NULL,
    ExpiryDate DATE NOT NULL,
    QuantityInStock INT NOT NULL,
    CostPrice DECIMAL(10,2) NOT NULL,
    SellingPrice DECIMAL(10,2) NOT NULL,
    SupplierID INT NOT NULL,
    SupplyDate DATE NOT NULL,
    PurchaseAmount DECIMAL(12,2) NOT NULL,
    PRIMARY KEY (MedicineID, BatchNo),
    FOREIGN KEY (MedicineID) REFERENCES MEDICINE(MedicineID) ON DELETE CASCADE,
    FOREIGN KEY (SupplierID) REFERENCES SUPPLIER(SupplierID)
);

CREATE TABLE PRESCRIPTION (
    PrescriptionID INT AUTO_INCREMENT PRIMARY KEY,
    PrescriptionImageURL VARCHAR(255),
    UploadDate DATE,
    UploadTime TIME,
    DoctorName VARCHAR(100),
    PatientName VARCHAR(100),
    VerificationStatus VARCHAR(30),
    PharmacistRemarks TEXT,
    Instructions TEXT,
    CustomerID INT,
    StaffID INT NULL,
    VerificationDate DATE NULL,
    VerificationTime TIME NULL,
    FOREIGN KEY (CustomerID) REFERENCES CUSTOMER(CustomerID) ON DELETE CASCADE,
    FOREIGN KEY (StaffID) REFERENCES STAFF(StaffID)
);

CREATE TABLE `ORDER` (
    OrderID INT AUTO_INCREMENT PRIMARY KEY,
    OrderDate DATE,
    OrderTime TIME,
    OrderType VARCHAR(20),
    OrderStatus VARCHAR(30),
    PaymentMethod VARCHAR(30),
    PaymentStatus VARCHAR(30),
    DiscountAmount DECIMAL(10,2),
    HouseNo VARCHAR(50),
    Street VARCHAR(100),
    City VARCHAR(50),
    Pincode VARCHAR(10),
    CustomerID INT NULL,
    StaffID INT NULL,
    PrescriptionID INT NULL,
    FOREIGN KEY (CustomerID) REFERENCES CUSTOMER(CustomerID),
    FOREIGN KEY (StaffID) REFERENCES STAFF(StaffID),
    FOREIGN KEY (PrescriptionID) REFERENCES PRESCRIPTION(PrescriptionID)
);

CREATE TABLE ORDER_ITEM (
    OrderID INT,
    ItemSeqNo INT,
    Quantity INT,
    UnitPriceAtSale DECIMAL(10,2),
    MedicineID INT,
    BatchNo VARCHAR(50),
    PRIMARY KEY (OrderID, ItemSeqNo),
    FOREIGN KEY (OrderID) REFERENCES `ORDER`(OrderID) ON DELETE CASCADE,
    FOREIGN KEY (MedicineID, BatchNo) REFERENCES BATCH(MedicineID, BatchNo)
);

CREATE TABLE ALERT (
    AlertID INT AUTO_INCREMENT PRIMARY KEY,
    AlertType VARCHAR(50),
    Message TEXT,
    Status VARCHAR(20),
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ResolvedAt TIMESTAMP NULL,
    MedicineID INT NULL,
    BatchNo VARCHAR(50) NULL,
    StaffID INT NULL,
    ResolvedDate DATE NULL,
    ActionTaken  TEXT,
    FOREIGN KEY (MedicineID, BatchNo) REFERENCES BATCH(MedicineID, BatchNo),
    FOREIGN KEY (StaffID) REFERENCES STAFF(StaffID)
);

-- ============================================================================
-- PERFORMANCE INDEXES (B-Tree Indexes for Query Optimization)
-- ============================================================================
CREATE INDEX idx_batch_expiry ON BATCH (MedicineID, ExpiryDate);
CREATE INDEX idx_order_date_status ON `ORDER` (OrderDate, OrderStatus);
CREATE INDEX idx_order_customer ON `ORDER` (CustomerID);
CREATE INDEX idx_orderitem_order ON ORDER_ITEM (OrderID);
CREATE INDEX idx_alert_status_med ON ALERT (Status, MedicineID);

-- ============================================================================
-- DATABASE VIEWS (Virtual Tables for Security & Reporting Abstraction)
-- ============================================================================
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
