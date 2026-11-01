# Abha Pharmacy — Feature Architecture & Future Roadmap

This document outlines the core features of the Abha Pharmacy system, explaining how they connect to one another, what inputs they require, and the automatic background processes they trigger. It also defines the roadmap for future enhancements.

---

## 1. Core Features & System Interconnectivity

### Feature 1: Customer Account Management
* **What it takes:** User's personal details (Name, Address, DoB, Gender) and an optional Phone Number.
* **Backend Automation:** The system automatically intercepts the raw password and securely hashes it before it ever touches the database. The system then generates a unique `CustomerID` and automatically links side-tables (like `CUSTOMER_PHONE`) to ensure 1NF database normalization.
* **Connected to:** **E-Commerce Cart** (for shipping addresses) and **Prescription Workflow** (to track patient history).

### Feature 2: Dynamic Inventory & Batch Tracking
* **What it takes:** Medicine details, Supplier Info, precise Batch Numbers, Expiry Dates, and Quantities.
* **Backend Automation (Race Condition Protection):** When staff restock items, the database uses an atomic `INSERT ... ON DUPLICATE KEY UPDATE` query. If two staff members restock the same batch at the exact same millisecond, the database mathematically adds the quantities together rather than throwing an error or overwriting data.
* **Connected to:** **Alert System** and **Order Fulfillment**.

### Feature 3: Automated Alert System
* **What it takes:** Nothing directly from the user (System-generated).
* **Backend Automation:** The system continuously monitors the `BATCH` tables in the background. 
  1. If `QuantityInStock` drops below a predefined threshold, it automatically generates a `LOW_STOCK` alert.
  2. If the `ExpiryDate` is within 30 days of the current system clock, it automatically generates a `NEAR_EXPIRY` alert.
* **Connected to:** **Inventory** and the **Admin Dashboard** (prompting staff to take action).

### Feature 4: E-Commerce Cart & FEFO Fulfillment
* **What it takes:** A digital cart containing multiple Medicine IDs and requested quantities.
* **Backend Automation (FEFO Logic & Atomic Guards):** 
  1. **FEFO (First-Expiry-First-Out):** The backend automatically sorts all available batches of a requested medicine by their Expiry Date. It deducts stock from the oldest (soonest to expire) batches first to prevent medicine waste.
  2. **Atomic Deduction:** It uses an SQL guard (`AND QuantityInStock >= ?`) to guarantee stock never drops below zero.
  3. It automatically generates a parent `ORDER` and splits the cart into multiple sequenced `ORDER_ITEM` rows.
* **Connected to:** **Customer Account**, **Inventory**, and **Billing**.

### Feature 5: Digital Prescription Workflow
* **What it takes:** An uploaded image file (JPG/PNG) of a physical prescription and patient instructions.
* **Backend Automation:** The image is securely routed to the Pharmacist's dashboard. When the Pharmacist approves it, the backend automatically transitions the workflow into the Cart system. It takes the pharmacist's dispensed medicines, dynamically creates an `ORDER`, deducts the stock, and attaches the `PharmacistRemarks` to the medical record.
* **Connected to:** **Customer Account**, **Inventory**, and **Orders**.

### Feature 6: Point of Sale (POS) Walk-in Sales
* **What it takes:** Medicine, specific Batch Number, Quantity, and Payment Method.
* **Backend Automation:** Instantly deducts stock from the specific physical batch chosen by the staff member, bypassing the FEFO routing since the staff physically grabbed the medicine from a specific rack.

---

## 2. Planned Improvements (Immediate Roadmap)

These are the immediate architectural improvements planned for the next major release:

### A. Advanced Authentication & Security
* **Current State:** The system relies on basic Session IDs and SHA-256 password hashing.
* **Improvement:** Upgrade to **JWT (JSON Web Tokens)** or **OAuth 2.0**.
* **Backend Automation:** Instead of keeping sessions in server memory, the server will issue a cryptographically signed JWT. This allows the backend to be entirely stateless (improving server speed). We can also implement Google Single Sign-On (SSO) or Mobile OTP verification.

### B. Payment Gateway Integration
* **Current State:** The system currently relies on Cash on Delivery (COD) for online orders.
* **Improvement:** Integrate the **Razorpay** or **Stripe** API.
* **Backend Automation:** When a customer checks out, the backend will generate a unique Order Token and redirect the user to the gateway. The system will expose a secure **Webhook Endpoint**. When the payment succeeds, Stripe/Razorpay will secretly ping this webhook, and the backend will automatically verify the digital signature and switch the `PaymentStatus` from `PENDING` to `PAID`.

### C. OpenCV & OCR for Auto-Prescription Reading
* **Current State:** Pharmacists must manually read the uploaded prescription images and manually select medicines to fulfill them.
* **Improvement:** Implement Computer Vision using **OpenCV** and **Tesseract OCR (Optical Character Recognition)**.
* **Backend Automation:** As soon as a customer uploads a prescription, a Python/AI microservice will automatically pre-process the image (using OpenCV to increase contrast and remove noise), then use OCR to read the doctor's handwriting/print. It will automatically match extracted text to the `MEDICINE` database and **pre-fill the pharmacist's cart** with suggested medicines, requiring the pharmacist only to click "Confirm" rather than doing manual data entry.

---

## 3. Future Scalability Recommendations (Long-Term Analysis)

To make this pharmacy system enterprise-ready, the following backend automations should be considered:

### A. Automated Email & SMS Notifications
* **Concept:** Integrate Twilio (SMS) and SendGrid (Email).
* **Automation:** Whenever the `OrderStatus` changes (e.g., from `PENDING` to `OUT_FOR_DELIVERY`), an asynchronous database event will automatically trigger a text message to the customer's phone number, keeping them informed without staff intervention.

### B. Automated Supplier Reordering (EOQ)
* **Concept:** Connect the Alert system to the Supplier database.
* **Automation:** When a `LOW_STOCK` alert is generated, the backend will calculate the Economic Order Quantity (EOQ) and automatically draft an email (or API call) to the linked `SUPPLIER` requesting a restock. The pharmacist only needs to click "Approve Purchase Order".

### C. Database Audit Logging via SQL Triggers
* **Concept:** Strict medical compliance (like HIPAA) requires knowing exactly who changed what data.
* **Automation:** Implement **MySQL Triggers**. If a staff member changes the `SellingPrice` of a medicine, the database engine will automatically and invisibly copy the old price, the new price, the `StaffID`, and the timestamp into a hidden `AUDIT_TRAIL` table to prevent internal fraud.

### D. Data Warehousing for Revenue Analytics (FR-16)
* **Concept:** Running heavy revenue reports on the live transaction database slows down the website for customers.
* **Automation:** Implement a nightly Cron Job that aggregates all completed `ORDER_ITEM` data at 2:00 AM, calculates total revenue, profit margins, and fast-moving drugs, and stores it in a separate Analytics table. The Admin Dashboard can then instantly load complex charts without affecting website speed.
