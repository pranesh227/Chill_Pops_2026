# Project: Chill Pops – Ice Cream POS & Shop Management System

Act as a Senior Full-Stack Developer, UI/UX Designer, Software Architect, and Database Engineer.

I want you to build a complete, professional, responsive Ice Cream Shop Point of Sale (POS) and Billing Management Website for my **Chill Pops franchise**.

This is an actual shop management application, not just a static landing page or demo UI.

## 1. Project Objective

Develop a user-friendly website where the shop owner can:
- View different ice cream varieties with product images and prices.
- Select products and quantities.
- Add products to a shopping cart.
- Automatically calculate the total bill.
- Generate a professional customer invoice.
- Display the shop's UPI payment QR code when the owner clicks Pay Now.
- Record completed sales with accurate transaction dates and times.
- Generate daily, monthly, and custom date-range sales reports.
- Add, edit, delete, and manage ice cream varieties independently.

## 2. Technology Stack

Use the following technologies:

Frontend:
- HTML5
- CSS3
- JavaScript
- Bootstrap 5 or Tailwind CSS

Backend:
- Java
- Spring Boot
- Spring Web
- Spring Data JPA
- Spring Security

Database:
- MySQL

Other integrations:
- QR Code generation
- PDF invoice generation
- Chart.js for sales analytics

Use a clean layered architecture with Controller, Service, Repository, Entity, DTO, and Configuration packages.

## 3. UI/UX Design – Chill Pops Theme

Create an attractive and modern ice cream franchise-inspired interface.

Design requirements:
- Pastel pink, cream, white, and light blue color palette.
- Modern typography.
- Rounded product cards.
- Smooth hover animations.
- Beautiful ice cream product images.
- Responsive layout for desktop, tablet, and mobile.
- Easy navigation and simple billing workflow.

Dashboard navigation:
1. Dashboard
2. Ice Cream Menu
3. Billing / POS
4. Cart
5. Sales History
6. Monthly Reports
7. Manage Products
8. Settings

## 4. Ice Cream Product Menu

Display ice creams in a beautiful product grid.

Each product card should contain:
- Product image
- Ice cream name
- Category
- Price in INR (₹)
- Availability status
- Add to Cart button

Initially include sample products from categories such as:
- Ice Cream Cups
- Popsicles
- Chocolate Ice Creams
- Fruit Ice Creams
- Sundaes
- Milkshakes
- Special Ice Creams

Use realistic sample products and prices.

IMPORTANT:
The shop owner must be able to add new products in the future without modifying the source code.

Create a Manage Products section with:
- Add Product
- Upload Product Image
- Product Name
- Category
- Price
- Description
- Stock / Availability
- Edit Product
- Delete Product

All products must be stored dynamically in MySQL.

## 5. POS Billing and Shopping Cart

When the shop owner selects an ice cream:
- Add it to the cart.
- Allow quantity increment and decrement.
- Allow item removal.
- Display individual product prices.
- Calculate subtotal automatically.
- Calculate total quantity.
- Calculate grand total dynamically.

Billing calculation:

Item Total = Product Price × Quantity

Grand Total = Sum of All Item Totals

Display the complete cart in a clean billing interface.

## 6. Invoice Generation

After selecting all products, provide a Generate Bill button.

Generate a professional invoice containing:

- Chill Pops franchise name and logo.
- Unique invoice number.
- Transaction date.
- Transaction time.
- Product names.
- Quantities.
- Individual prices.
- Item-wise totals.
- Subtotal.
- Applicable tax, if configured.
- Grand total.
- Payment status.

Provide options:
- Print Bill
- Download Invoice as PDF
- Proceed to Payment

Do not apply GST automatically without configuring the applicable tax rate.

## 7. UPI QR Payment Integration

When the owner clicks Pay Now:

- Display the shop's configured UPI QR code.
- Show the total payable amount clearly.
- Display instructions for customers to scan and pay.
- Allow the owner to confirm payment after verifying the payment in their UPI application.
- Store the payment method and payment status.

Provide Settings to configure:
- Shop UPI ID
- Shop name
- Payment QR code image

IMPORTANT:
Do not automatically mark a transaction as paid merely because the customer scans the QR code. Actual payment verification must happen before marking it as completed.

## 8. Transaction Management

Every completed transaction must be stored in MySQL.

Store:
- Unique transaction ID
- Invoice number
- Transaction date
- Exact transaction time
- Purchased products
- Product quantities
- Total amount
- Payment method
- Payment status

Use proper date-time handling and store timestamps consistently.

Display transactions in a sales history table.

Allow searching by:
- Invoice number
- Transaction date
- Payment status

## 9. Daily and Monthly Sales Reports

Create a professional Sales Reports dashboard.

Report filters:
- Today's Sales
- Yesterday's Sales
- This Month
- Previous Month
- Custom Date Range

Monthly report must display:
- Selected month and year.
- Total number of transactions.
- Total products sold.
- Total revenue from completed sales.
- Total pending payments.
- Best-selling ice cream.
- Category-wise sales.
- Daily revenue breakdown.

Add interactive charts using Chart.js.

IMPORTANT:
When the owner selects a particular month, retrieve only transactions belonging to that month and year.

For example:
If the owner selects October 2026, show transactions from 01-10-2026 to 31-10-2026, using the shop's local timezone.

Allow downloading monthly reports as PDF or Excel.

## 10. Dashboard

Create a visually attractive dashboard containing:

- Today's Revenue
- Today's Orders
- Monthly Revenue
- Total Products
- Best-Selling Ice Cream
- Recent Transactions
- Sales Performance Chart

Use real database information rather than hardcoded values.

## 11. Database Design

Create suitable MySQL tables:

1. users
2. products
3. categories
4. transactions
5. transaction_items
6. payment_details
7. shop_settings

Define appropriate primary keys, foreign keys, relationships, indexes, and constraints.

Store product prices and transaction amounts using BigDecimal in Java and DECIMAL in MySQL.

Maintain transaction item snapshots so historical invoices remain accurate even if product names or prices change later.

## 12. Security

- Create a secure shop-owner login.
- Protect dashboard and management pages.
- Only authorized users can manage products and view reports.
- Validate all backend inputs.
- Never expose database credentials in frontend code.

## 13. Development Approach

Develop the application in a structured way.

Phase 1: Project setup and database configuration.

Phase 2: Create the responsive Chill Pops frontend.

Phase 3: Implement product management.

Phase 4: Implement POS cart and billing calculations.

Phase 5: Implement invoice generation and printing.

Phase 6: Implement UPI QR payment workflow.

Phase 7: Implement transaction history.

Phase 8: Implement daily and monthly sales reports.

Phase 9: Testing, validation, and final UI improvements.

## 14. Important Development Instructions

- Do not create only a frontend prototype.
- Implement actual backend APIs and MySQL integration.
- Do not hardcode products or sales reports.
- Ensure all buttons and forms work.
- Use reusable components and clean code.
- Handle errors and invalid inputs properly.
- Maintain accurate transaction timestamps.
- Make the website responsive.
- Provide complete source code with proper folder structure.
- Include MySQL database scripts.
- Include application.properties configuration.
- Include API endpoints and their explanations.
- Include step-by-step instructions to run the project locally.

Start by explaining the proposed architecture and folder structure. Then build the application module by module.

Do not skip functionality or replace backend features with static mock data.

The final result should look and work like a real commercial ice cream shop POS application.