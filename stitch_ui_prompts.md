# Stitch UI Generation Prompts

Here are the detailed, strict prompts you can feed into Stitch to generate your UI. I have restricted these prompts to **only** include features that are already supported by your Spring Boot backend controllers (no extra features).

You can either use the **Global App Prompt** to describe the entire project to Stitch, or use the **Individual Screen Prompts** to generate them one by one.

---

## Global App Prompt (For Project Creation)

> **Prompt:**
> Build a UI for "Abha Pharmacy", a pharmacy management web application with a modern, clean medical theme (e.g., teal/blue and white). The application has two main portals: a Customer Portal and an Admin/Staff Portal. Do not add any features beyond what is explicitly requested. 
> 
> **Customer Portal requirements:**
> - Landing Page: Simple hero section with links to Customer Login, Customer Register, and Admin Login.
> - Authentication: Login (Email, Password) and Registration (First/Last Name, Email, Password, Phone, Address, DOB).
> - Dashboard: View recent orders, past prescriptions, and featured OTC medicines.
> - Medicine Catalog: Search bar and list of OTC medicines. Clicking a medicine shows its details.
> - Cart & Checkout: A page showing selected medicines with quantity inputs, and a button to place the order.
> - Prescription Upload: A form with a file upload field for the prescription image, a text area for instructions, and a submit button.
> - Order Tracking: A simple list displaying past and current orders with their fulfillment status.
> - Refill: A page listing past prescriptions with an option to request a refill.
> 
> **Admin/Pharmacist Portal requirements:**
> - Authentication: Login (Email, Password).
> - Admin Dashboard: A comprehensive dashboard showing four main lists: Active Alerts (low stock/expiry), Pending Prescriptions, Current Inventory, and Active Orders. Includes a search bar to find medicines and view their batches.
> - Point of Sale (POS): A checkout interface for in-store sales. Needs inputs to select a medicine and batch, enter quantity, unit price, discount amount, and select a payment method, followed by a Checkout button.
> - Prescription Fulfillment: A page to review an uploaded prescription, add remarks, and dynamically select medicines and quantities to approve and fulfill the prescription.
> - Inventory Management (GRN): Forms to add a new Medicine (Name, Category, Manufacturer), a new Batch (Medicine, Supplier, Batch No, Mfg/Exp Date, Prices, Stock), and a new Supplier.
> - Staff Management (Owner only): A list of current staff, a form to add new staff (Name, Email, Role, Password), and a button to deactivate staff.
> - Orders Management: A page to view all active orders and mark them as complete.

---

## Individual Screen Prompts (For generating page by page)

If you are generating screens individually, use these targeted prompts:

### 1. Landing & Authentication
**Landing Page (`index.html`)**
> Create a clean landing page for Abha Pharmacy. It should have a welcoming hero section and three prominent buttons: "Customer Login", "Register as Customer", and "Staff Login". Keep it simple and professional.

**Customer Login / Admin Login**
> Create a modern login page for a pharmacy portal. It needs an email input, a password input, a "Sign In" button, and a link to register (for customers). Show a placeholder for an error message.

**Customer Registration**
> Create a registration form for pharmacy customers. Fields required: First Name, Last Name, Email, Password, Phone Number, Address, Date of Birth. Add a "Register" submit button.

### 2. Customer Portal
**Customer Dashboard**
> Create a customer dashboard for a pharmacy. It should have a sidebar or top navigation. The main content area should display three sections: "My Recent Orders", "My Prescriptions", and a quick view of "Available OTC Medicines". 

**Medicine Catalog & Details**
> Create a catalog page for Over-The-Counter (OTC) medicines. It needs a search bar at the top and a grid of medicine cards (showing name, manufacturer, and price). 

**Prescription Upload**
> Create a prescription upload page. It should have a clean form containing a file upload area (drag and drop style), a text area for "Special Instructions", and a "Submit Prescription" button.

**Cart & Checkout**
> Create a simple shopping cart page. It should display a list of selected items where the user can adjust the quantity (number input). Below the list, include a summary section and a "Place Order" button. 

### 3. Admin / Staff Portal
**Admin Dashboard**
> Create a pharmacy admin dashboard. It needs a sidebar navigation. The main view should have summary cards or tables for: "Low Stock/Expiry Alerts", "Pending Prescriptions", and "Active Orders". Include a search bar at the top to search the inventory.

**Point of Sale (POS)**
> Create a Point of Sale (POS) interface for a pharmacist. It should have a form to ring up a sale: dropdowns for Medicine and Batch Number, number inputs for Quantity, Unit Price, and Discount Amount, a dropdown for Payment Method (Cash, Card, UPI), and a large "Checkout" button.

**Prescription Fulfillment**
> Create an admin page for fulfilling a prescription. The left side should show the uploaded prescription image/details. The right side should have a form to add a "Remarks" note, a dynamic list where the pharmacist can add medicines and specify their quantities, and an "Approve & Generate Order" button.

**Inventory Forms (GRN / Add items)**
> Create a tabbed interface for pharmacy inventory management. Tab 1: "Add Medicine" (Name, Category, Manufacturer). Tab 2: "Add Batch" (Medicine, Supplier, Batch No, Mfg Date, Exp Date, Buy Price, Sell Price, Initial Stock). Tab 3: "Add Supplier" (Name, Contact Person, Phone, Email, Address). All tabs should have a submit button.

**Staff Management**
> Create a staff management page for the pharmacy owner. It should display a table of current staff members with a "Deactivate" action button. Below the table, include a form to add a new staff member with fields for First Name, Last Name, Email, Role (dropdown), Phone, and Password.
