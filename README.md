# Inventory Management System 📦

A full-stack enterprise-grade B2B/B2C Inventory Management System built to completely digitize and automate daily supply chain operations. It features real-time automated stock tracking, dynamic smart UI rendering, mathematical financial precision, and robust ACID-compliant database transaction processing.

## 🚀 Tech Stack

*   **Frontend:** React.js, Vite, Axios, React Router
*   **Backend:** Java, Spring Boot, Spring Data JPA, Spring Security
*   **Database:** MySQL
*   **Architecture:** RESTful APIs, Layered Architecture, DTO Pattern

## ✨ Key Technical Features

1.  **Automated Real-Time Stock Tracking**
    *   Purchases automatically *increase* `opening_stock`.
    *   Sales (Billing) automatically *decrease* `opening_stock`.
2.  **ACID Transaction Rollback**
    *   Utilizes Spring Boot `@Transactional` to ensure that if a single item fails to save during a massive bulk transaction, the *entire* transaction rolls back, guaranteeing 100% database integrity without corrupt data.
3.  **True Server-Side Pagination**
    *   Implements `Pageable` backend queries (`LIMIT` and `OFFSET`) coupled with debounced search APIs to handle hundreds of thousands of records effortlessly without crashing the browser.
4.  **Dynamic Smart UI Rendering**
    *   The React frontend intelligently scans line items on the fly. If all line items in a receipt have 0% GST or 0 Discount, the UI completely hides those columns and summary rows to present a clean invoice.
5.  **Strict Data Transfer Object (DTO) Pattern**
    *   Raw database entities are never exposed to the client. Strict Request/Response DTO wrappers prevent infinite recursion in JSON serialization and harden API security.
6.  **Financial Mathematical Precision**
    *   All financial variables (Unit Price, Tax, Net Amount) strictly use Java's `BigDecimal` instead of `Double` or `Float` to prevent catastrophic micro-rounding errors.

## 📂 Project Structure

This is a monorepo containing both the Frontend and Backend services.

*   `/inventory-management/` - The Spring Boot Java backend application.
*   `/inventory-frontend/` - The Vite + React.js frontend application.

## ⚙️ How to Run Locally

### 1. Database Setup
Create a new MySQL database named `inventory_db` and ensure your local MySQL server is running on port `3306` with the username `root` and password `Root@12345` (or update `application.properties`).

### 2. Run the Spring Boot Backend
```bash
cd inventory-management
mvn spring-boot:run
```
*The backend server will start on port `8080`.*

### 3. Run the React Frontend
```bash
cd inventory-frontend
npm install
npm run dev
```
*The frontend application will start on `http://localhost:5173`.*
