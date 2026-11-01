-- ============================================================================
-- Abha Clinical Pharmacy & Dispensary Portal - Enterprise Seed Data
-- Fully Populated Dataset: Owner, Clinical Staff, Verified Patients,
-- 15 Authentic Medicines, Batch Inventories, Prescriptions, Orders & Alerts
-- ============================================================================

SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE ALERT;
TRUNCATE TABLE ORDER_ITEM;
TRUNCATE TABLE `ORDER`;
TRUNCATE TABLE PRESCRIPTION;
TRUNCATE TABLE BATCH;
TRUNCATE TABLE MEDICINE;
TRUNCATE TABLE SUPPLIER_PHONE;
TRUNCATE TABLE SUPPLIER;
TRUNCATE TABLE STAFF_PHONE;
TRUNCATE TABLE STAFF;
TRUNCATE TABLE CUSTOMER_PHONE;
TRUNCATE TABLE CUSTOMER;
SET FOREIGN_KEY_CHECKS = 1;

-- Password Hash Reference:
-- 'Password@123' -> ff7bd97b1a7789ddd2775122fd6817f3173672da9f802ceec57f284325bf589f
-- 'password123' -> ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f

-- ----------------------------------------------------------------------------
-- 1. STAFF & OWNER ROSTER (1 Owner + 5 Healthcare / Dispensary Staff)
-- ----------------------------------------------------------------------------
INSERT INTO STAFF (StaffID, FirstName, LastName, Email, PasswordHash, Role, LicenseNumber, HireDate, Salary) VALUES
(1, 'Haras', 'Kumar', 'owner@abhapharmacy.com', 'ff7bd97b1a7789ddd2775122fd6817f3173672da9f802ceec57f284325bf589f', 'OWNER', 'OW-IN-2024-001', '2020-01-01', 150000.00),
(2, 'Rajesh', 'Sharma', 'rajesh.pharmacist@abhapharmacy.com', 'ff7bd97b1a7789ddd2775122fd6817f3173672da9f802ceec57f284325bf589f', 'PHARMACIST', 'PCI-DL-49201', '2021-03-15', 68000.00),
(3, 'Priya', 'Verma', 'priya.verma@abhapharmacy.com', 'ff7bd97b1a7789ddd2775122fd6817f3173672da9f802ceec57f284325bf589f', 'PHARMACIST', 'PCI-MH-84192', '2022-07-01', 58000.00),
(4, 'Amit', 'Patel', 'amit.billing@abhapharmacy.com', 'ff7bd97b1a7789ddd2775122fd6817f3173672da9f802ceec57f284325bf589f', 'STAFF', 'STF-POS-104', '2023-02-10', 38000.00),
(5, 'Vikram', 'Malhotra', 'vikram.grn@abhapharmacy.com', 'ff7bd97b1a7789ddd2775122fd6817f3173672da9f802ceec57f284325bf589f', 'STAFF', 'STF-INV-208', '2023-05-18', 42000.00),
(6, 'Ananya', 'Sen', 'ananya.sen@abhapharmacy.com', 'ff7bd97b1a7789ddd2775122fd6817f3173672da9f802ceec57f284325bf589f', 'PHARMACIST', 'PCI-WB-33910', '2023-09-01', 60000.00);

INSERT INTO STAFF_PHONE (StaffID, PhoneNumber) VALUES
(1, '9327090309'),
(2, '9168821562'),
(3, '9820011223'),
(4, '9820033445'),
(5, '9820055667'),
(6, '9820077889');

-- ----------------------------------------------------------------------------
-- 2. PATIENT / CUSTOMER BASE (12 Realistic Verified Indian Patients)
-- ----------------------------------------------------------------------------
INSERT INTO CUSTOMER (CustomerID, FirstName, LastName, HouseNo, Street, City, Pincode, Email, PasswordHash, DateOfBirth, Gender, RegistrationDate) VALUES
(1, 'Aarav', 'Mehta', 'Flat 402, Lotus Towers', 'Connaught Circus', 'New Delhi', '110001', 'aarav.mehta@gmail.com', 'ff7bd97b1a7789ddd2775122fd6817f3173672da9f802ceec57f284325bf589f', '1988-04-12', 'Male', '2023-01-15'),
(2, 'Sneha', 'Kulkarni', 'B-14, Shanti Vihar', 'FC Road, Deccan', 'Pune', '411004', 'sneha.kulkarni@gmail.com', 'ff7bd97b1a7789ddd2775122fd6817f3173672da9f802ceec57f284325bf589f', '1992-09-23', 'Female', '2023-02-10'),
(3, 'Rohan', 'Gupta', '12/A, Marine Drive Apt', 'Marine Lines', 'Mumbai', '400020', 'rohan.gupta@yahoo.com', 'ff7bd97b1a7789ddd2775122fd6817f3173672da9f802ceec57f284325bf589f', '1985-11-30', 'Male', '2023-03-05'),
(4, 'Pooja', 'Nair', 'Villa 8, Green Meadows', 'Indiranagar 100ft Rd', 'Bengaluru', '560038', 'pooja.nair@outlook.com', 'ff7bd97b1a7789ddd2775122fd6817f3173672da9f802ceec57f284325bf589f', '1995-06-18', 'Female', '2023-03-22'),
(5, 'Mohd', 'Faisal Khan', 'H-92, Hazratganj Lane', 'MG Marg', 'Lucknow', '226001', 'faisal.khan@gmail.com', 'ff7bd97b1a7789ddd2775122fd6817f3173672da9f802ceec57f284325bf589f', '1990-08-05', 'Male', '2023-04-12'),
(6, 'Anjali', 'Deshmukh', 'Plot 55, Ramdaspeth', 'Wardha Road', 'Nagpur', '440010', 'anjali.deshmukh@gmail.com', 'ff7bd97b1a7789ddd2775122fd6817f3173672da9f802ceec57f284325bf589f', '1987-03-14', 'Female', '2023-05-01'),
(7, 'Karan', 'Joshi', '701, Riviera Heights', 'Satellite Road', 'Ahmedabad', '380015', 'karan.joshi@gmail.com', 'ff7bd97b1a7789ddd2775122fd6817f3173672da9f802ceec57f284325bf589f', '1993-12-02', 'Male', '2023-06-18'),
(8, 'Divya', 'Sundaram', 'Flat 3C, Palm Grove', 'Anna Nagar West', 'Chennai', '600040', 'divya.sundaram@gmail.com', 'ff7bd97b1a7789ddd2775122fd6817f3173672da9f802ceec57f284325bf589f', '1991-07-25', 'Female', '2023-07-09'),
(9, 'Suresh', 'Bose', '44/1, Salt Lake Sector 2', 'Bidhannagar', 'Kolkata', '700091', 'suresh.bose@gmail.com', 'ff7bd97b1a7789ddd2775122fd6817f3173672da9f802ceec57f284325bf589f', '1979-02-17', 'Male', '2023-08-14'),
(10, 'Neha', 'Kapoor', 'House 112, Sector 18', 'Madhya Marg', 'Chandigarh', '160018', 'neha.kapoor@gmail.com', 'ff7bd97b1a7789ddd2775122fd6817f3173672da9f802ceec57f284325bf589f', '1996-10-09', 'Female', '2023-09-20'),
(11, 'Aditya', 'Singhania', 'B-4, Malviya Nagar', 'Tonk Road', 'Jaipur', '302017', 'aditya.singhania@gmail.com', 'ff7bd97b1a7789ddd2775122fd6817f3173672da9f802ceec57f284325bf589f', '1984-05-31', 'Male', '2023-10-11'),
(12, 'Sunita', 'Rao', 'Flat 502, Kakatiya Res', 'Banjara Hills Rd 12', 'Hyderabad', '500034', 'sunita.rao@gmail.com', 'ff7bd97b1a7789ddd2775122fd6817f3173672da9f802ceec57f284325bf589f', '1975-01-20', 'Female', '2023-11-04');

INSERT INTO CUSTOMER_PHONE (CustomerID, PhoneNumber) VALUES
(1, '9811223344'),
(2, '9822334455'),
(3, '9833445566'),
(4, '9844556677'),
(5, '9855667788'),
(6, '9866778899'),
(7, '9877889900'),
(8, '9888990011'),
(9, '9899001122'),
(10, '9911002233'),
(11, '9922113344'),
(12, '9933224455');

-- ----------------------------------------------------------------------------
-- 3. AUTHORIZED SUPPLIERS (4 Genuine Pharmaceutical Distributors)
-- ----------------------------------------------------------------------------
INSERT INTO SUPPLIER (SupplierID, SupplierName, ContactPerson, Email, GSTIN, Street, City, State, Pincode) VALUES
(1, 'PharmaCorp Logistics', 'Bob Jones', 'bob@pharmacorp.com', '22AAAAA0000A1Z5', 'Industrial Estate', 'Mumbai', 'Maharashtra', '400001'),
(2, 'Sun Pharma National Hub', 'Sanjay Chawla', 'distribution@sunpharma.in', '24AAACS1234F1Z8', 'GIDC Estate, Makarpura', 'Vadodara', 'Gujarat', '390010'),
(3, 'Cipla Supply Chain Ltd', 'Dr. Ramesh Nair', 'orders@cipla-dist.com', '27AAACC4567G1Z2', 'Bhosari Industrial Area', 'Pune', 'Maharashtra', '411026'),
(4, 'Mankind Mediserve Pvt Ltd', 'Deepak Agarwal', 'supply@mankindmediserve.in', '07AAACM8901H1Z4', 'Okhla Industrial Phase 3', 'New Delhi', 'Delhi', '110020');

INSERT INTO SUPPLIER_PHONE (SupplierID, PhoneNumber) VALUES
(1, '022-24901234'),
(2, '0265-2654321'),
(3, '020-27129876'),
(4, '011-41605555');

-- ----------------------------------------------------------------------------
-- 4. 15 AUTHENTIC MEDICINES ACROSS THERAPEUTIC SPECIALTIES
-- ----------------------------------------------------------------------------
INSERT INTO MEDICINE (MedicineID, MedicineName, GenericName, Manufacturer, Category, RequiresPrescription, ReorderThreshold, StorageCondition, Description) VALUES
(1, 'Paracetamol 500mg', 'Paracetamol / Acetaminophen', 'PharmaCorp Logistics', 'Analgesic', FALSE, 50, 'Room Temperature (< 25°C)', 'First-line antipyretic and mild-to-moderate pain reliever formulation.'),
(2, 'Amoxicillin 500mg', 'Amoxicillin Trihydrate', 'Cipla Supply Chain Ltd', 'Antibiotic', TRUE, 40, 'Cool Dry Place (< 20°C)', 'Broad-spectrum beta-lactam antibiotic for bacterial respiratory & ENT infections. Schedule H Prescription Drug.'),
(3, 'Metformin 500mg SR', 'Metformin Hydrochloride', 'Sun Pharma National Hub', 'Antidiabetic', TRUE, 60, 'Room Temperature', 'Oral antihyperglycemic medication for Type 2 diabetes glycemic control. Schedule H.'),
(4, 'Amlodipine 5mg', 'Amlodipine Besylate', 'Sun Pharma National Hub', 'Antihypertensive', TRUE, 40, 'Store protected from light', 'Dihydropyridine calcium channel blocker used in hypertension and coronary artery disease.'),
(5, 'Pantoprazole 40mg', 'Pantoprazole Sodium', 'Mankind Mediserve Pvt Ltd', 'Antacid / PPI', FALSE, 50, 'Store below 25°C', 'Proton pump inhibitor that reduces gastric acid production for GERD & peptic ulcers.'),
(6, 'Azithromycin 500mg', 'Azithromycin Dihydrate', 'Cipla Supply Chain Ltd', 'Antibiotic', TRUE, 30, 'Cool Dry Place', 'Macrolide antibiotic active against respiratory tract, skin, and soft tissue infections. Schedule H.'),
(7, 'Cetirizine 10mg', 'Cetirizine Hydrochloride', 'PharmaCorp Logistics', 'Antiallergic', FALSE, 50, 'Room Temperature', 'Second-generation non-sedating antihistamine for seasonal rhinitis, urticaria & dust allergies.'),
(8, 'Atorvastatin 10mg', 'Atorvastatin Calcium', 'Sun Pharma National Hub', 'Cardiovascular', TRUE, 35, 'Store protected from moisture', 'HMG-CoA reductase inhibitor (statin) for hypercholesterolemia and cardiovascular prevention. Schedule H.'),
(9, 'Telmisartan 40mg', 'Telmisartan', 'Cipla Supply Chain Ltd', 'Antihypertensive', TRUE, 40, 'Store in original packaging', 'Angiotensin II receptor antagonist (ARB) for arterial hypertension management. Schedule H.'),
(10, 'Ibuprofen 400mg', 'Ibuprofen', 'Mankind Mediserve Pvt Ltd', 'NSAID Analgesic', FALSE, 45, 'Store in cool place', 'Nonsteroidal anti-inflammatory drug providing rapid analgesic & anti-inflammatory relief.'),
(11, 'Montelukast 10mg', 'Montelukast Sodium', 'Cipla Supply Chain Ltd', 'Respiratory', TRUE, 30, 'Protect from light', 'Leukotriene receptor antagonist indicated for chronic bronchial asthma and allergic rhinitis. Schedule H.'),
(12, 'Omeprazole 20mg', 'Omeprazole', 'Sun Pharma National Hub', 'Gastrointestinal', FALSE, 40, 'Store in cool place', 'Delayed-release capsule formulation for heartburn, acid reflux, and mucosal healing.'),
(13, 'Ciprofloxacin 500mg', 'Ciprofloxacin Hydrochloride', 'PharmaCorp Logistics', 'Antibiotic', TRUE, 25, 'Room Temperature', 'Broad-spectrum fluoroquinolone antibiotic for severe urinary tract, bone, and systemic infections. Schedule H.'),
(14, 'Vitamin D3 60K IU', 'Cholecalciferol', 'Mankind Mediserve Pvt Ltd', 'Nutritional', FALSE, 50, 'Store in cool dry place', 'High-potency chewable cholecalciferol formulation for bone mineral density and immune replenishment.'),
(15, 'Benadryl Cough Formula 100ml', 'Dextromethorphan + Diphenhydramine', 'PharmaCorp Logistics', 'Cough Syrup', FALSE, 35, 'Do not freeze', 'Dual-action soothing expectorant and antitussive syrup for dry and allergic coughing fits.');

-- ----------------------------------------------------------------------------
-- 5. BATCH INVENTORIES (Long Future Expiry Dates: 2027 to 2029)
-- ----------------------------------------------------------------------------
INSERT INTO BATCH (MedicineID, BatchNo, RackLocation, MfgDate, ExpiryDate, QuantityInStock, CostPrice, SellingPrice, SupplierID, SupplyDate, PurchaseAmount) VALUES
(1, 'BATCH-PCM-24', 'Rack A1', '2024-01-10', '2028-11-02', 850, 1.20, 2.50, 1, '2024-01-20', 1020.00),
(2, 'BATCH-AMX-24', 'Rack A2', '2024-02-15', '2028-08-30', 420, 6.50, 12.00, 3, '2024-02-28', 2730.00),
(3, 'BATCH-MET-25', 'Rack B1', '2024-03-01', '2028-12-31', 600, 2.10, 4.50, 2, '2024-03-15', 1260.00),
(4, 'BATCH-AML-25', 'Rack B2', '2024-03-10', '2028-10-15', 480, 1.80, 3.80, 2, '2024-03-22', 864.00),
(5, 'BATCH-PAN-25', 'Rack B3', '2024-04-05', '2029-04-30', 550, 4.00, 8.50, 4, '2024-04-18', 2200.00),
(6, 'BATCH-AZI-24', 'Rack A3', '2024-04-12', '2027-11-30', 320, 14.50, 28.00, 3, '2024-04-25', 4640.00),
(7, 'BATCH-CET-25', 'Rack C1', '2024-05-01', '2029-05-31', 750, 1.10, 2.20, 1, '2024-05-15', 825.00),
(8, 'BATCH-ATO-25', 'Rack B4', '2024-05-10', '2028-09-30', 380, 5.20, 11.50, 2, '2024-05-22', 1976.00),
(9, 'BATCH-TEL-25', 'Rack B5', '2024-05-20', '2029-01-15', 450, 4.80, 9.80, 3, '2024-06-02', 2160.00),
(10, 'BATCH-IBU-24', 'Rack C2', '2024-06-01', '2028-06-30', 620, 1.50, 3.20, 4, '2024-06-15', 930.00),
(11, 'BATCH-MON-25', 'Rack C3', '2024-06-10', '2028-12-15', 290, 8.00, 16.50, 3, '2024-06-25', 2320.00),
(12, 'BATCH-OME-25', 'Rack B6', '2024-06-18', '2029-03-31', 410, 3.20, 6.80, 2, '2024-07-01', 1312.00),
(13, 'BATCH-CIP-24', 'Rack A4', '2024-07-02', '2027-12-31', 260, 9.50, 18.00, 1, '2024-07-14', 2470.00),
(14, 'BATCH-VD3-25', 'Rack D1', '2024-07-15', '2029-07-31', 500, 18.00, 35.00, 4, '2024-07-28', 9000.00),
(15, 'BATCH-COF-25', 'Rack D2', '2024-08-01', '2028-08-31', 350, 45.00, 85.00, 1, '2024-08-15', 15750.00);

-- ----------------------------------------------------------------------------
-- 6. SAMPLE CLINICAL PRESCRIPTIONS (Verified & In-Queue)
-- ----------------------------------------------------------------------------
INSERT INTO PRESCRIPTION (PrescriptionID, PrescriptionImageURL, UploadDate, UploadTime, DoctorName, PatientName, VerificationStatus, PharmacistRemarks, Instructions, CustomerID, StaffID, VerificationDate, VerificationTime) VALUES
(1, '/images/sample_rx_1.png', '2024-10-15', '10:30:00', 'Dr. R. K. Singhal, MD', 'Aarav Mehta', 'VERIFIED', 'Prescription validated against National Formulary. Dispense Amoxicillin 500mg TDS for 5 days.', 'Take 1 capsule every 8 hours after meals.', 1, 2, '2024-10-15', '11:00:00'),
(2, '/images/sample_rx_2.png', '2024-10-18', '14:15:00', 'Dr. Suniti Verma, Cardiologist', 'Rohan Gupta', 'VERIFIED', 'Schedule H compliance cleared. Blood pressure monitoring advised.', 'Amlodipine 5mg once daily in morning.', 3, 3, '2024-10-18', '14:45:00'),
(3, '/images/sample_rx_3.png', '2024-10-22', '16:00:00', 'Dr. M. S. Gill, Endocrinologist', 'Sneha Kulkarni', 'VERIFIED', 'Metformin 500mg SR after dinner. Fasting blood sugar log requested.', '1 tablet daily with evening meals.', 2, 2, '2024-10-22', '16:30:00'),
(4, '/images/sample_rx_4.png', '2024-11-01', '09:20:00', 'Dr. Arvind Saxena, Pulmonologist', 'Mohd Faisal Khan', 'PENDING', 'Awaiting clinical review by Duty Pharmacist Dr. Rajesh.', 'Azithromycin 500mg OD x 3 days + Montelukast 10mg HS.', 5, NULL, NULL, NULL);

-- ----------------------------------------------------------------------------
-- 7. RECENT TRANSACTIONS & COMPLETED ORDERS (Active Pharmacy Revenue Data)
-- ----------------------------------------------------------------------------
INSERT INTO `ORDER` (OrderID, OrderDate, OrderTime, OrderType, OrderStatus, PaymentMethod, PaymentStatus, DiscountAmount, HouseNo, Street, City, Pincode, CustomerID, StaffID, PrescriptionID, TransactionRef) VALUES
(1001, '2024-10-15', '11:15:00', 'ONLINE_DELIVERY', 'DELIVERED', 'UPI', 'PAID', 10.00, 'Flat 402, Lotus Towers', 'Connaught Circus', 'New Delhi', '110001', 1, 2, 1, 'TXN-UPI-984210'),
(1002, '2024-10-18', '15:00:00', 'ONLINE_DELIVERY', 'DELIVERED', 'UPI', 'PAID', 0.00, '12/A, Marine Drive Apt', 'Marine Lines', 'Mumbai', '400020', 3, 3, 2, 'TXN-UPI-984211'),
(1003, '2024-10-22', '16:45:00', 'COUNTER_PICKUP', 'DELIVERED', 'CARD', 'PAID', 5.00, 'B-14, Shanti Vihar', 'FC Road, Deccan', 'Pune', '411004', 2, 4, 3, 'TXN-POS-440192'),
(1004, '2024-10-28', '18:20:00', 'ONLINE_DELIVERY', 'DELIVERED', 'UPI', 'PAID', 15.00, 'Villa 8, Green Meadows', 'Indiranagar 100ft Rd', 'Bengaluru', '560038', 4, 2, NULL, 'TXN-UPI-984215'),
(1005, '2024-11-01', '19:10:00', 'ONLINE_DELIVERY', 'CONFIRMED', 'UPI', 'PAID', 0.00, 'Flat 3C, Palm Grove', 'Anna Nagar West', 'Chennai', '600040', 8, 3, NULL, 'TXN-UPI-984220');

-- ----------------------------------------------------------------------------
-- 8. ORDER ITEMS (Line Items Linked to Orders & Batches)
-- ----------------------------------------------------------------------------
INSERT INTO ORDER_ITEM (OrderID, ItemSeqNo, Quantity, UnitPriceAtSale, MedicineID, BatchNo) VALUES
(1001, 1, 10, 12.00, 2, 'BATCH-AMX-24'),
(1001, 2, 10, 2.50, 1, 'BATCH-PCM-24'),
(1002, 1, 30, 3.80, 4, 'BATCH-AML-25'),
(1003, 1, 60, 4.50, 3, 'BATCH-MET-25'),
(1004, 1, 4, 35.00, 14, 'BATCH-VD3-25'),
(1004, 2, 2, 85.00, 15, 'BATCH-COF-25'),
(1005, 1, 20, 8.50, 5, 'BATCH-PAN-25'),
(1005, 2, 20, 2.20, 7, 'BATCH-CET-25');

-- ----------------------------------------------------------------------------
-- 9. INVENTORY ALERTS (Active Operational Traceability)
-- ----------------------------------------------------------------------------
INSERT INTO ALERT (AlertID, AlertType, Message, Status, CreatedAt, ResolvedAt, MedicineID, BatchNo, StaffID, ResolvedDate, ActionTaken) VALUES
(1, 'LOW_STOCK', 'Ciprofloxacin 500mg inventory reached reorder threshold (260 units remaining).', 'ACTIVE', '2024-10-30 08:30:00', NULL, 13, 'BATCH-CIP-24', NULL, NULL, NULL),
(2, 'NEAR_EXPIRY', 'Azithromycin Batch BATCH-AZI-24 scheduled for standard quarterly stock rotation audit.', 'RESOLVED', '2024-10-20 09:00:00', '2024-10-21 11:30:00', 6, 'BATCH-AZI-24', 5, '2024-10-21', 'Verified sealed packaging; rack transferred to Front Shelf Priority for fast dispensation.');
