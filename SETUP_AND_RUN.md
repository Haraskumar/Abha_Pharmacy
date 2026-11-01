# Setup and Run Guide for Abha Pharmacy

This guide provides step-by-step instructions on how to set up the database, configure the application, and run the Abha Pharmacy website locally on your computer.

---

## 🛠️ Prerequisites

Before you begin, ensure you have the following installed on your machine:

1. **Java Development Kit (JDK) 17 or higher**
   - Verify installation by running: `java -version` in your command prompt.
2. **Maven** (Optional, as the project includes a Maven Wrapper `mvnw.cmd`)
3. **MySQL Server**
   - Must be running locally on the default port `3306`.
   - You need a tool to run SQL commands (like MySQL Workbench, phpMyAdmin, or the MySQL command line).

---

## ⚙️ Step 1: Database Setup

The Spring Boot application needs a database to connect to. 

1. Open MySQL Workbench or your command line.
2. Run the following SQL command to create the blank database:
   ```sql
   CREATE DATABASE abha_pharmacy;
   ```
*(Note: You do not need to manually create the tables. Spring Boot will automatically read `schema.sql` and create all tables for you when you run the app!)*

---

## 🔗 Step 2: Configure Application Credentials

The application needs to know your MySQL username and password to connect.

1. Open the project folder in your IDE or text editor.
2. Navigate to: `src/main/resources/application.properties`
3. Find the database configuration lines:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/abha_pharmacy?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
   spring.datasource.username=Abha_Pharmacy
   spring.datasource.password=pharmacy@123
   ```
4. **Change the username and password** to match your local MySQL server (e.g., username `root` and your personal root password).

### Important Database Initialization Rule:
In that same `application.properties` file, look for `spring.sql.init.mode`.
- Set it to **`always`** the VERY FIRST TIME you run the project so it creates the tables and inserts the dummy data from `schema.sql` and `data.sql`.
- After you successfully run it once and the tables are created, change it back to **`never`**. If you leave it on `always`, it will wipe your database and restart fresh every time you turn the server on!

---

## 🚀 Step 3: Run the Application

You can run the application directly from your terminal using the included Maven wrapper.

1. Open PowerShell or Command Prompt.
2. Navigate to the root folder of the project (`D:\IIT_BHU\3rd Year\CSE302\Project\Abha_Pharmacy`).
3. Run the following command:
   ```bash
   .\mvnw.cmd clean spring-boot:run
   ```
4. Wait for the terminal to download dependencies and compile. When you see a message like `Started AbhaPharmacyApplication in X seconds`, the server is running!

---

## 🌐 Step 4: Access the Website

Open your web browser and navigate to the following URLs:

* **Main Website / Homepage:**
  [http://localhost:8081/](http://localhost:8081/)
  *From here, customers can browse medicines, register, and login.*

* **Pharmacist Admin Dashboard:**
  [http://localhost:8081/login/admin](http://localhost:8081/login/admin)
  *From here, staff can manage inventory, process prescriptions, and handle POS sales.*

---

## 🛑 Troubleshooting

* **Error: `Port 8081 is already in use`**
  - Another application is using port 8081. Open `application.properties` and change `server.port=8081` to `server.port=8080` (or any other free port).

* **Error: `Access denied for user` or `Failed to configure a DataSource`**
  - You entered the wrong MySQL username or password in `application.properties`, or your MySQL server is not turned on. Double-check your credentials.

* **Error: `Failed to determine a suitable driver class`**
  - You ran the `mvnw` command from the wrong folder. You MUST be inside the `Abha_Pharmacy` root directory where the `pom.xml` file is located before running the command.
