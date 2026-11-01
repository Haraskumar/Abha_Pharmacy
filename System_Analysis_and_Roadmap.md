# Abha Pharmacy - Database & Architecture Analysis

This document provides a complete breakdown of the Database constraints, ACID properties, Normalization structure, and a technical roadmap for future integrations.

---

## 1. Database Normalization Analysis
The database is structured to meet **Third Normal Form (3NF)** standards to ensure zero data redundancy.

* **1NF (First Normal Form - Atomic Values):** 
  We separated phone numbers into `CUSTOMER_PHONE`, `STAFF_PHONE`, and `SUPPLIER_PHONE` tables instead of storing multiple numbers in a single comma-separated column.
* **2NF (Second Normal Form - No Partial Dependency):**
  In the `ORDER_ITEM` table, the Primary Key is composite: `(OrderID, ItemSeqNo)`. Details like `OrderDate` or `PaymentStatus` depend only on the `OrderID` (not the `ItemSeqNo`), so they are strictly kept in the `ORDER` table.
* **3NF (Third Normal Form - No Transitive Dependency):**
  The `BATCH` table contains `MedicineID`, but it does *not* duplicate the `MedicineName` or `Category`. If we need the name, we use an SQL `JOIN` with the `MEDICINE` table. This means if a Medicine's name changes, we only update one row in the whole database.

---

## 2. ACID Properties Implementation
Our backend strictly follows ACID properties to prevent data corruption during simultaneous user actions.

* **Atomicity (All or Nothing):** 
  Using Spring's `@Transactional` annotation (e.g., in `OrderService.placeCartOrder()`), if a customer tries to buy 5 medicines but the 5th one fails due to lack of stock, the **entire** transaction rolls back. No partial orders are ever saved.
* **Consistency (Valid States):** 
  The schema enforces structural consistency via `FOREIGN KEY` constraints (e.g., an Order cannot belong to a non-existent CustomerID) and `UNIQUE` constraints (e.g., no duplicate Emails).
* **Isolation (Concurrency Control):** 
  When updating stock, we use the `UPDATE BATCH SET QuantityInStock = QuantityInStock - ? WHERE QuantityInStock >= ?` SQL command. This isolates concurrent POS checkouts, guaranteeing that if two pharmacists sell the exact same medicine at the exact same millisecond, the database resolves it sequentially.
* **Durability (Permanent Storage):** 
  Once an order is successfully committed, it is written to the MySQL InnoDB storage engine on disk. A power failure a second later will not erase the transaction.

---

## 3. General Medical Validation Constraints (Fixed)
In a pharmacy, invalid numbers can have severe physical and financial consequences. I have added strict Java-level guards to prevent the following invalid medical values from entering the database:

1. **Negative Stock & Prices:** `QuantityInStock`, `CostPrice`, and `SellingPrice` are mathematically guarded against being $< 0$.
2. **Time-Traveling Expiry Dates:** When adding a new batch, the system now throws an `IllegalArgumentException` if:
   * The `MfgDate` (Manufacturing Date) is chronologically *after* the `ExpiryDate`.
   * The `ExpiryDate` is in the past (preventing the entry of already-expired stock into the system).
3. **Negative POS Pricing:** POS discounts and unit prices are strictly enforced to be $\ge 0$.

---

## 4. Planned Advanced Improvements

### A. Authentication (Transition to Stateless Security)
* **Current State:** Basic SHA-256 hashing and server-side memory sessions (`HttpSession`).
* **Technical Improvement:** Upgrade to **JWT (JSON Web Tokens)**.
* **Backend Flow:** When a user logs in, the backend signs a payload with an HMAC-SHA256 secret key and returns a JWT. The frontend stores this token in an `HttpOnly` cookie. This makes the backend completely stateless, allowing horizontal scaling (adding more Tomcat servers) without losing user sessions. We can also integrate OAuth2 (Google Sign-in).

### B. Payment Integration (Automated Webhooks)
* **Current State:** Cash On Delivery (COD) and manual POS tracking.
* **Technical Improvement:** Integrate **Razorpay** or **Stripe** APIs.
* **Backend Flow:** 
  1. On checkout, the backend creates a `PENDING` order and requests a transaction token from the Razorpay API.
  2. The frontend loads the Razorpay widget using this token.
  3. Once paid, Razorpay sends an asynchronous secure POST request to our `/api/webhooks/payment` endpoint.
  4. The backend verifies the cryptographic signature of the webhook and automatically updates the `ORDER` table `PaymentStatus` to `PAID`.

### C. Image Prescription via OpenCV & Tesseract OCR
* **Current State:** Pharmacists manually read uploaded prescription JPGs.
* **Technical Improvement:** Automated Computer Vision pipeline.
* **Backend Flow:**
  1. User uploads a prescription image.
  2. The Java backend passes the image to a Python microservice via HTTP.
  3. Python uses **OpenCV** to apply Grayscale, Gaussian Blur, and Adaptive Thresholding to make the doctor's handwriting clearer.
  4. **Tesseract OCR** scans the processed image for text.
  5. Python runs a fuzzy-string-matching algorithm against our `MEDICINE` database to identify prescribed drugs.
  6. The Java backend receives this list and automatically pre-fills the pharmacist's fulfillment cart with the suggested medicines and quantities.

### D. Further Recommended Implementations
1. **Automated Supplier Reordering (EOQ):** When stock hits the `ReorderThreshold`, automatically email a Purchase Order to the `SUPPLIER` using the Economic Order Quantity algorithm.
2. **Audit Logging Triggers:** Use MySQL `AFTER UPDATE` triggers to log every change made to `SellingPrice` or `Role` into an invisible `AUDIT_TRAIL` table to prevent internal fraud.
