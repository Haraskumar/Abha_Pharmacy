INSERT INTO CUSTOMER (FirstName, LastName, HouseNo, Street, City, Pincode, Email, PasswordHash, DateOfBirth, Gender, RegistrationDate)
VALUES ('John', 'Doe', '12A', 'Main Street', 'Delhi', '110001', 'john@example.com', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', '1990-01-15', 'Male', '2023-01-10');

INSERT INTO STAFF (FirstName, LastName, Email, PasswordHash, Role, LicenseNumber, HireDate, Salary)
VALUES ('Jane', 'Smith', 'jane@abhapharmacy.com', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'PHARMACIST', 'PH-12345', '2022-05-20', 50000.00);

INSERT INTO STAFF (FirstName, LastName, Email, PasswordHash, Role, LicenseNumber, HireDate, Salary)
VALUES ('Super', 'Admin', 'owner@abhapharmacy.com', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'OWNER', 'OW-00001', '2020-01-01', 100000.00);

INSERT INTO SUPPLIER (SupplierName, ContactPerson, Email, GSTIN, Street, City, State, Pincode)
VALUES ('PharmaCorp', 'Bob Jones', 'bob@pharmacorp.com', '22AAAAA0000A1Z5', 'Industrial Estate', 'Mumbai', 'Maharashtra', '400001');

INSERT INTO MEDICINE (MedicineName, GenericName, Manufacturer, Category, RequiresPrescription, ReorderThreshold, StorageCondition, Description)
VALUES ('Paracetamol 500mg', 'Paracetamol', 'PharmaCorp', 'Analgesic', FALSE, 20, 'Room Temperature', 'Pain reliever');

INSERT INTO MEDICINE (MedicineName, GenericName, Manufacturer, Category, RequiresPrescription, ReorderThreshold, StorageCondition, Description)
VALUES ('Amoxicillin 250mg', 'Amoxicillin', 'PharmaCorp', 'Antibiotic', TRUE, 15, 'Cool Dry Place', 'Broad spectrum antibiotic');

INSERT INTO BATCH (MedicineID, BatchNo, RackLocation, MfgDate, ExpiryDate, QuantityInStock, CostPrice, SellingPrice, SupplierID, SupplyDate, PurchaseAmount)
VALUES (1, 'B001', 'A1', '2023-01-01', '2025-12-31', 100, 2.00, 2.50, 1, '2023-02-01', 200.00);

INSERT INTO BATCH (MedicineID, BatchNo, RackLocation, MfgDate, ExpiryDate, QuantityInStock, CostPrice, SellingPrice, SupplierID, SupplyDate, PurchaseAmount)
VALUES (2, 'B002', 'B2', '2023-05-01', '2024-05-01', 5, 8.00, 10.00, 1, '2023-06-01', 40.00);
