# CHILL POPS – Stick Ice Cream Franchise POS, Billing & Admin Management System 🍦✨

A complete, modern, mobile-responsive Ice Cream Franchise Point of Sale (POS), Billing, and Admin Management Website built for **CHILL POPS**.

---

## 🌟 Brand & Design Specifications

- **Brand Name**: **CHILL POPS**
- **Homepage Tagline**: *"Cool Your Day with the Chill Pops Way!"*
- **Welcome Heading**: *"Welcome to Chill Pops!"*
- **Supporting Text**: *"Discover delicious flavours, refreshing frozen treats, and happiness in every stick. Make every moment sweeter with your favourite Chill Pops!"*
- **Color Palette**: Pastel Pink (`#FF6584`, `#FFE4E8`), Ice Blue (`#38BDF8`, `#E0F2FE`, `#0284C7`), Cream (`#FFFDF7`), White, and Soft Peach (`#FFF1E6`).
- **Strict Product Policy**: **Handcrafted Stick Ice Creams & Popsicles ONLY**.
  - No cones, cups, sundaes, milkshakes, tubs, or bowls.
  - Price bounds: Strictly **₹10.00 to ₹50.00** enforced and validated on frontend and backend.

---

## 🚀 Key Modules & Capabilities

### 1. Customer Ice Cream Menu & Catalog
- Modern responsive grid displaying stick ice creams and popsicles.
- Real-time live search filter and category pill selectors:
  - *Fruit Pops*
  - *Choco Sticks*
  - *Cream Sticks*
  - *Exotic & Premium Sticks*
- Dual-image fallback: High-res artisanal photo with SVG stick vector fallback to prevent broken URLs.
- 1-click **Add to Cart** with visual cart counter badge.

### 2. POS Express Billing Terminal
- Cashier split-screen layout: Quick-select stick grid on the left, live cart and bill summary on the right.
- Stepper buttons (`+`/`-`) with dynamic quantity calculation and item removal.
- **Dynamic GST Calculation**:
  - **GST Disabled by Default**: No tax line is shown; Subtotal = Grand Total.
  - **GST Enabled**: Real-time calculation of subtotal, configurable GST rate %, GST amount, and grand total.
- **Historical Invoice Immutability**: Modifying settings or prices never alters past invoices.
- **Payment Method**: Cash or Dynamic UPI.

### 3. UPI QR Code & Manual Payment Verification
- Dynamic NPCI-compliant UPI QR code generation (`upi://pay?pa=...&pn=...&am=...&tr=...`).
- Scannable with Google Pay, PhonePe, Paytm, BHIM, Cred, etc.
- Support for custom franchise bank QR standee image upload.
- Displays exact payable amount and franchise UPI ID with a 1-click copy button.
- **Manual Owner Verification**: Transactions are only marked `COMPLETED` once verified by the cashier/owner.

### 4. Professional Invoice Generation & Thermal Printing
- Daily formatted sequential invoice numbers (`INV-YYYYMMDD-0001`).
- Itemized breakdown (Flavour name, Category, Qty, Unit Price, Line Total).
- Subtotal, GST details (only if enabled on that invoice), and Grand Total.
- **1-Click Print Invoice** (thermal and A4 paper printer compatible).
- **Download Invoice PDF** generated via OpenPDF (`/api/billing/{id}/pdf`).

### 5. Sales Reports & Interactive Analytics
- Pre-built presets: Today, Yesterday, This Month, and Custom Date Range.
- Metrics: Orders count, Total sticks sold, Completed revenue, Pending revenue, Best-selling flavour.
- **Interactive Chart.js Visualizations**:
  - Daily sales trend bar chart.
  - Category revenue distribution doughnut chart.
- **Export Options**:
  - Export to **Excel (.xlsx)** via Apache POI.
  - Export to **PDF (.pdf)** via OpenPDF.
- All timestamps formatted using **Asia/Kolkata** timezone.

### 6. Admin Settings – Dynamic Configuration
- **GST Configuration**: Enable/Disable toggle switch, configurable GST percentage (`taxRate`).
- **UPI Configuration**: UPI ID / VPA, Payee Account Name, Custom QR code image upload.
- **Shop Information**: Franchise name, physical address, contact phone, customer invoice footer message.
- Saved permanently in MySQL; changes immediately reflect on the POS terminal without code modifications.

### 7. Product Management (Admin)
- Add new stick ice cream varieties with name, category, price (₹10–₹50), description, and image.
- Edit existing flavours and adjust prices.
- 1-click In-Stock / Unavailable toggle.
- Safe soft-delete to preserve transaction history integrity.

---

## 🛠️ Technology Stack

- **Backend**: Java 21 LTS, Spring Boot 3.3.5, Spring Web, Spring Data JPA, Spring Security 6
- **Database**: MySQL 8.0 with InnoDB
- **Frontend**: HTML5, CSS3, JavaScript (ES6+), Bootstrap 5.3.3, Chart.js 4.4, Font Awesome 6.5
- **Libraries**:
  - `com.google.zxing:javase:3.5.3` (UPI QR Code generation)
  - `com.github.librepdf:openpdf:1.3.43` (PDF invoices and PDF sales reports)
  - `org.apache.poi:poi-ooxml:5.3.0` (Excel .xlsx sales report export)

---

## 📁 Project Directory Structure

```
E:\Chill_pops\
├── pom.xml                                    # Maven configuration & dependencies
├── schema.sql                                 # Complete MySQL database schema & sample seed data
├── uploads\                                   # Custom user-uploaded stick photos & QR codes
├── src\
│   └── main\
│       ├── java\com\chillpops\
│       │   ├── ChillPopsApplication.java      # Spring Boot main class
│       │   ├── config\
│       │   │   ├── AppConfig.java             # Clock, ZoneId (Asia/Kolkata), BCrypt PasswordEncoder
│       │   │   ├── DataInitializer.java       # Seed admin user, settings, categories & stick flavours
│       │   │   ├── SecurityConfig.java        # Spring Security 6 authorization & CSRF configuration
│       │   │   └── WebMvcConfig.java          # Static file resource handlers & /uploads/** mapping
│       │   ├── controller\
│       │   │   ├── AuthController.java        # Login, logout, auth status
│       │   │   ├── BillingController.java     # Bill creation, confirmation, QR code & PDF receipt
│       │   │   ├── ProductController.java     # Products & categories CRUD, image upload
│       │   │   ├── ReportController.java      # Sales reports dashboard, Excel & PDF export
│       │   │   ├── SettingsController.java    # GST, UPI & store settings, QR code upload
│       │   │   └── TransactionController.java # Transaction search, filters & details
│       │   ├── dto\
│       │   │   ├── AuthDtos.java              # Login and User info records
│       │   │   ├── BillDtos.java              # POS Bill request, item snapshot & response records
│       │   │   ├── ProductDtos.java           # Product request & response records (₹10-₹50 validation)
│       │   │   ├── ReportDto.java             # Dashboard metrics & sales report records
│       │   │   └── SettingsDto.java           # Dynamic shop configuration records
│       │   ├── entity\
│       │   │   ├── Category.java              # categories entity
│       │   │   ├── InvoiceSequence.java       # invoice_sequence entity
│       │   │   ├── PaymentDetail.java         # payment_details entity
│       │   │   ├── Product.java               # products entity
│       │   │   ├── ShopSetting.java           # shop_settings entity
│       │   │   ├── Tx.java                    # transactions entity
│       │   │   ├── TxItem.java                # transaction_items entity (snapshot pricing)
│       │   │   ├── TxStatus.java              # PENDING, COMPLETED, CANCELLED enum
│       │   │   └── User.java                  # users entity
│       │   ├── exception\
│       │   │   ├── BusinessException.java     # Custom runtime exception with HTTP status
│       │   │   └── GlobalExceptionHandler.java# Centralized JSON error response handler
│       │   ├── repository\                    # Spring Data JPA repositories
│       │   └── service\
│       │       ├── BillingService.java        # Core billing, tax calculation & payment verification
│       │       ├── CustomUserDetailsService.java
│       │       ├── InvoiceNumberService.java  # Thread-safe daily sequence generator
│       │       ├── PdfInvoiceService.java     # OpenPDF invoice generator
│       │       ├── ProductService.java        # Product inventory management & validation
│       │       ├── QrCodeService.java         # ZXing UPI QR code generation
│       │       ├── ReportExportService.java   # Apache POI Excel & OpenPDF report exporter
│       │       ├── ReportService.java         # Daily/monthly sales analytics & best seller
│       │       └── SettingsService.java       # Franchise settings & QR upload service
│       └── resources\
│           ├── application.properties         # Server port (8080), MySQL credentials, upload limits
│           └── static\
│               ├── index.html                 # Complete Single Page Application
│               ├── css\style.css              # Custom Chill Pops theme & print CSS
│               ├── js\app.js                  # Frontend state management & Chart.js integration
│               └── images\
│                   ├── logo.svg               # Chill Pops brand logo
│                   └── products\              # 18 stick ice cream photos & SVG fallbacks
```

---

## ⚡ How to Run the Application

### 1. Database Setup
Ensure MySQL is running on `localhost:3306`, then initialize the database:
```bash
mysql -u root -p < schema.sql
```
*(Enter your MySQL root password, e.g. `2005` or your local password)*.

### 2. Verify Database Credentials
Open `src/main/resources/application.properties` and verify your credentials:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/chillpops?createDatabaseIfNotExist=true&useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Kolkata
spring.datasource.username=root
spring.datasource.password=2005
```

### 3. Compile and Run
Run using Maven:
```bash
mvn clean package -DskipTests
java -jar target/pos-1.0.0.jar
```

Or run directly with the Spring Boot Maven plugin:
```bash
mvn spring-boot:run
```

The application will start on **`http://localhost:8080`**.

### 4. Admin Sign-In Credentials
- **Username**: `admin`
- **Password**: `admin123`
*(A convenient 1-Click Auto Fill button is provided on the login modal)*.

---

## 🧪 Verification & Automated Testing

To run the complete automated 22-step verification test suite:
```bash
python test_chillpops.py
```
Test results verify:
- Homepage branding & tagline ("Cool Your Day with the Chill Pops Way!")
- Strict stick ice cream validation (₹10 to ₹50 price bounds)
- Admin authentication & role assignment
- Dynamic GST enable/disable toggling and correct tax calculations
- Historical invoice immutability
- UPI dynamic QR code generation
- Cash and UPI payment verification
- Customer invoice PDF generation
- Sales reports, Chart.js visualizations, and Excel/PDF export
- File upload for custom stick images and bank QR codes
