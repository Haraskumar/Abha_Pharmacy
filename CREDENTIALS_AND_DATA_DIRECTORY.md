# 🏥 Abha Pharmacy — Comprehensive Credentials & Enterprise Data Directory
*Generated on 2026-11-02 for Lab Demonstration & Production Staging*

This document provides a single-source-of-truth directory of all pre-populated test data across Abha Pharmacy: **Owner Account**, **Clinical & Dispensary Staff**, **Registered Patients**, **Authentic Medicines**, **Batch Inventories**, and **Simulated Clinical Prescriptions & Orders**.

---

## 🔑 1. Authentication Cheat Sheet (All Passwords)

> **Master Password for all Demo Accounts:** `Password@123`
> *(Also backward-compatible with legacy `password123`)*

---

## 🛡️ 2. Owner & Administrative Staff Directory

| ID | Full Name | Email (Login ID) | Password | System Role | License / Staff Code | Phone / Helpline | Salary |
| :---: | :--- | :--- | :--- | :---: | :---: | :---: | :---: |
| **1** | **Haras Kumar** | `owner@abhapharmacy.com` | `Password@123` | **OWNER** | `OW-IN-2024-001` | **`9327090309`** | ₹1,50,000 |
| **2** | **Dr. Rajesh Sharma** | `rajesh.pharmacist@abhapharmacy.com` | `Password@123` | **PHARMACIST** | `PCI-DL-49201` | **`9168821562`** | ₹68,000 |
| **3** | **Priya Verma** | `priya.verma@abhapharmacy.com` | `Password@123` | **PHARMACIST** | `PCI-MH-84192` | `9820011223` | ₹58,000 |
| **4** | **Amit Patel** | `amit.billing@abhapharmacy.com` | `Password@123` | **STAFF (POS)** | `STF-POS-104` | `9820033445` | ₹38,000 |
| **5** | **Vikram Malhotra** | `vikram.grn@abhapharmacy.com` | `Password@123` | **STAFF (GRN)** | `STF-INV-208` | `9820055667` | ₹42,000 |
| **6** | **Dr. Ananya Sen** | `ananya.sen@abhapharmacy.com` | `Password@123` | **PHARMACIST** | `PCI-WB-33910` | `9820077889` | ₹60,000 |

* **Admin Portal Login URL:** `http://localhost:8081/login/admin`
* **Owner Permissions:** Full system access, Staff salary/deactivation, POS billing, Goods Receipt (GRN), Prescription fulfillment, Schedule H register, and financial reporting.

---

## 👥 3. Registered Patients / Customers Directory

| ID | Patient Name | Email (Login ID) | Password | City / State | Contact Number | Gender | Date of Birth |
| :---: | :--- | :--- | :--- | :--- | :---: | :---: | :---: |
| **1** | **Aarav Mehta** | `aarav.mehta@gmail.com` | `Password@123` | New Delhi | `9811223344` | Male | 1988-04-12 |
| **2** | **Sneha Kulkarni** | `sneha.kulkarni@gmail.com` | `Password@123` | Pune, MH | `9822334455` | Female | 1992-09-23 |
| **3** | **Rohan Gupta** | `rohan.gupta@yahoo.com` | `Password@123` | Mumbai, MH | `9833445566` | Male | 1985-11-30 |
| **4** | **Pooja Nair** | `pooja.nair@outlook.com` | `Password@123` | Bengaluru, KA | `9844556677` | Female | 1995-06-18 |
| **5** | **Mohd. Faisal Khan** | `faisal.khan@gmail.com` | `Password@123` | Lucknow, UP | `9855667788` | Male | 1990-08-05 |
| **6** | **Anjali Deshmukh** | `anjali.deshmukh@gmail.com` | `Password@123` | Nagpur, MH | `9866778899` | Female | 1987-03-14 |
| **7** | **Karan Joshi** | `karan.joshi@gmail.com` | `Password@123` | Ahmedabad, GJ | `9877889900` | Male | 1993-12-02 |
| **8** | **Divya Sundaram** | `divya.sundaram@gmail.com` | `Password@123` | Chennai, TN | `9888990011` | Female | 1991-07-25 |
| **9** | **Suresh Bose** | `suresh.bose@gmail.com` | `Password@123` | Kolkata, WB | `9899001122` | Male | 1979-02-17 |
| **10** | **Neha Kapoor** | `neha.kapoor@gmail.com` | `Password@123` | Chandigarh | `9911002233` | Female | 1996-10-09 |
| **11** | **Aditya Singhania** | `aditya.singhania@gmail.com` | `Password@123` | Jaipur, RJ | `9922113344` | Male | 1984-05-31 |
| **12** | **Sunita Rao** | `sunita.rao@gmail.com` | `Password@123` | Hyderabad, TS | `9933224455` | Female | 1975-01-20 |

* **Patient Portal Login URL:** `http://localhost:8081/login/customer`
* **Patient Capabilities:** Medicine catalog search, OTC ordering, UPI/COD checkout, Prescription upload, Refill re-orders, Order tracking, and GST invoice download.

---

## 💊 4. Medicine Formulation & Batch Inventory Matrix

All 15 formulations are populated with active, non-expired batch inventory (Expiry: **2027 to 2029**):

| ID | Medicine Name | Generic Name | Category | Rx Flag | Batch No | Expiry Date | In Stock | Cost (₹) | MRP (₹) | Rack Location |
| :---: | :--- | :--- | :--- | :---: | :---: | :---: | :---: | :---: | :---: | :---: |
| **1** | **Paracetamol 500mg** | Acetaminophen | Analgesic | **OTC** | `BATCH-PCM-24` | **2028-11-02** | 850 | ₹1.20 | ₹2.50 | Rack A1 |
| **2** | **Amoxicillin 500mg** | Amoxicillin Trihydrate | Antibiotic | **Schedule H** | `BATCH-AMX-24` | **2028-08-30** | 420 | ₹6.50 | ₹12.00 | Rack A2 |
| **3** | **Metformin 500mg SR** | Metformin HCl | Antidiabetic | **Schedule H** | `BATCH-MET-25` | **2028-12-31** | 600 | ₹2.10 | ₹4.50 | Rack B1 |
| **4** | **Amlodipine 5mg** | Amlodipine Besylate | Antihypertensive | **Schedule H** | `BATCH-AML-25` | **2028-10-15** | 480 | ₹1.80 | ₹3.80 | Rack B2 |
| **5** | **Pantoprazole 40mg** | Pantoprazole Sodium | Antacid / PPI | **OTC** | `BATCH-PAN-25` | **2029-04-30** | 550 | ₹4.00 | ₹8.50 | Rack B3 |
| **6** | **Azithromycin 500mg** | Azithromycin Dihydrate | Antibiotic | **Schedule H** | `BATCH-AZI-24` | **2027-11-30** | 320 | ₹14.50 | ₹28.00 | Rack A3 |
| **7** | **Cetirizine 10mg** | Cetirizine HCl | Antiallergic | **OTC** | `BATCH-CET-25` | **2029-05-31** | 750 | ₹1.10 | ₹2.20 | Rack C1 |
| **8** | **Atorvastatin 10mg** | Atorvastatin Calcium | Cardiovascular | **Schedule H** | `BATCH-ATO-25` | **2028-09-30** | 380 | ₹5.20 | ₹11.50 | Rack B4 |
| **9** | **Telmisartan 40mg** | Telmisartan | Antihypertensive | **Schedule H** | `BATCH-TEL-25` | **2029-01-15** | 450 | ₹4.80 | ₹9.80 | Rack B5 |
| **10** | **Ibuprofen 400mg** | Ibuprofen | NSAID Analgesic | **OTC** | `BATCH-IBU-24` | **2028-06-30** | 620 | ₹1.50 | ₹3.20 | Rack C2 |
| **11** | **Montelukast 10mg** | Montelukast Sodium | Respiratory | **Schedule H** | `BATCH-MON-25` | **2028-12-15** | 290 | ₹8.00 | ₹16.50 | Rack C3 |
| **12** | **Omeprazole 20mg** | Omeprazole | Gastrointestinal | **OTC** | `BATCH-OME-25` | **2029-03-31** | 410 | ₹3.20 | ₹6.80 | Rack B6 |
| **13** | **Ciprofloxacin 500mg** | Ciprofloxacin HCl | Antibiotic | **Schedule H** | `BATCH-CIP-24` | **2027-12-31** | 260 | ₹9.50 | ₹18.00 | Rack A4 |
| **14** | **Vitamin D3 60K IU** | Cholecalciferol | Nutritional | **OTC** | `BATCH-VD3-25` | **2029-07-31** | 500 | ₹18.00 | ₹35.00 | Rack D1 |
| **15** | **Benadryl Syrup 100ml** | Dextromethorphan | Cough Formula | **OTC** | `BATCH-COF-25` | **2028-08-31** | 350 | ₹45.00 | ₹85.00 | Rack D2 |

---

## 🏭 5. Authorized Pharmaceutical Suppliers

| ID | Supplier Name | GSTIN | Contact Person | Email | Location |
| :---: | :--- | :---: | :--- | :--- | :--- |
| **1** | **PharmaCorp Logistics** | `22AAAAA0000A1Z5` | Bob Jones | `bob@pharmacorp.com` | Mumbai, Maharashtra |
| **2** | **Sun Pharma National Hub** | `24AAACS1234F1Z8` | Sanjay Chawla | `distribution@sunpharma.in` | Vadodara, Gujarat |
| **3** | **Cipla Supply Chain Ltd** | `27AAACC4567G1Z2` | Dr. Ramesh Nair | `orders@cipla-dist.com` | Pune, Maharashtra |
| **4** | **Mankind Mediserve Pvt Ltd** | `07AAACM8901H1Z4` | Deepak Agarwal | `supply@mankindmediserve.in` | Okhla, New Delhi |

---

## 📋 6. Pre-Seeded Prescriptions & Order History

* **Verified Prescriptions:**
  - Prescription #1 (Patient: Aarav Mehta, Doctor: Dr. R. K. Singhal, MD) -> Amoxicillin 500mg
  - Prescription #2 (Patient: Rohan Gupta, Doctor: Dr. Suniti Verma) -> Amlodipine 5mg
  - Prescription #3 (Patient: Sneha Kulkarni, Doctor: Dr. M. S. Gill) -> Metformin 500mg SR
* **Pending Prescription (For AI Verification Demo):**
  - Prescription #4 (Patient: Mohd. Faisal Khan, Doctor: Dr. Arvind Saxena) -> *Ready in queue at `/admin/prescriptions`!*
* **Past Settled Orders:**
  - Order #1001 (Aarav Mehta - ₹145.00 via UPI `TXN-UPI-984210`)
  - Order #1002 (Rohan Gupta - ₹114.00 via UPI `TXN-UPI-984211`)
  - Order #1003 (Sneha Kulkarni - ₹270.00 via POS Card `TXN-POS-440192`)
  - Order #1004 (Pooja Nair - ₹310.00 via UPI `TXN-UPI-984215`)
  - Order #1005 (Divya Sundaram - ₹214.00 via UPI `TXN-UPI-984220`)
* **Live Alerts:**
  - Alert #1: Low Stock warning on Ciprofloxacin 500mg (`BATCH-CIP-24`).
  - Alert #2: Audit resolution log on Azithromycin 500mg.
