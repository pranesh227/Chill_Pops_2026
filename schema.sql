CREATE DATABASE IF NOT EXISTS chillpops CHARACTER SET utf8mb4;
USE chillpops;

CREATE TABLE IF NOT EXISTS users (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50) NOT NULL UNIQUE,
  password_hash VARCHAR(100) NOT NULL,
  role VARCHAR(20) NOT NULL DEFAULT 'OWNER',
  enabled BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS categories (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(60) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS products (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  description VARCHAR(500),
  category_id BIGINT NOT NULL,
  price DECIMAL(10,2) NOT NULL CHECK (price >= 10.00 AND price <= 50.00),
  image_path VARCHAR(255),
  available BOOLEAN NOT NULL DEFAULT TRUE,
  stock INT NULL,
  deleted BOOLEAN NOT NULL DEFAULT FALSE,
  FOREIGN KEY(category_id) REFERENCES categories(id),
  INDEX idx_prod_cat(category_id)
);

CREATE TABLE IF NOT EXISTS invoice_sequence (
  day_key CHAR(8) PRIMARY KEY,
  last_no INT NOT NULL
);

CREATE TABLE IF NOT EXISTS transactions (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  invoice_number VARCHAR(30) NOT NULL UNIQUE,
  idempotency_key VARCHAR(64) NOT NULL UNIQUE,
  status VARCHAR(15) NOT NULL, -- PENDING | COMPLETED | CANCELLED
  subtotal DECIMAL(12,2) NOT NULL,
  tax_rate DECIMAL(5,2) NOT NULL DEFAULT 0.00,
  tax_amount DECIMAL(12,2) NOT NULL DEFAULT 0.00,
  grand_total DECIMAL(12,2) NOT NULL,
  created_at DATETIME(3) NOT NULL,
  completed_at DATETIME(3) NULL,
  INDEX idx_tx_status_completed(status, completed_at),
  INDEX idx_tx_created(created_at)
);

CREATE TABLE IF NOT EXISTS transaction_items (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  transaction_id BIGINT NOT NULL,
  product_id BIGINT NULL,
  product_name VARCHAR(100) NOT NULL,
  category_name VARCHAR(60) NOT NULL,
  unit_price DECIMAL(10,2) NOT NULL,
  quantity INT NOT NULL CHECK (quantity > 0),
  line_total DECIMAL(12,2) NOT NULL,
  FOREIGN KEY(transaction_id) REFERENCES transactions(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS payment_details (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  transaction_id BIGINT NOT NULL UNIQUE,
  method VARCHAR(15) NOT NULL,
  upi_reference VARCHAR(64),
  confirmed_by VARCHAR(50) NOT NULL,
  confirmed_at DATETIME(3) NOT NULL,
  FOREIGN KEY(transaction_id) REFERENCES transactions(id)
);

CREATE TABLE IF NOT EXISTS shop_settings (
  id TINYINT PRIMARY KEY DEFAULT 1,
  shop_name VARCHAR(100) NOT NULL DEFAULT 'Chill Pops',
  upi_id VARCHAR(80) DEFAULT 'chillpops@oksbi',
  upi_account_name VARCHAR(100) DEFAULT 'Chill Pops Franchise',
  qr_image_path VARCHAR(255) NULL,
  tax_rate DECIMAL(5,2) NOT NULL DEFAULT 5.00,
  gst_enabled BOOLEAN NOT NULL DEFAULT FALSE,
  shop_address VARCHAR(255) DEFAULT '123 Popsicle Street, Chill Zone, Bengaluru, Karnataka 560001',
  contact_number VARCHAR(30) DEFAULT '+91 98765 43210',
  logo_path VARCHAR(255) DEFAULT '/images/logo.svg',
  invoice_footer VARCHAR(255) DEFAULT 'Thank you for choosing Chill Pops! Cool your day the Chill Pops way!',
  CHECK (id = 1)
);

INSERT INTO shop_settings (id, shop_name, upi_id, upi_account_name, tax_rate, gst_enabled, shop_address, contact_number, logo_path, invoice_footer)
VALUES (1, 'Chill Pops', 'chillpops@oksbi', 'Chill Pops Franchise', 5.00, FALSE, '123 Popsicle Street, Chill Zone, Bengaluru, Karnataka 560001', '+91 98765 43210', '/images/logo.svg', 'Thank you for choosing Chill Pops! Cool your day the Chill Pops way!')
ON DUPLICATE KEY UPDATE shop_name = VALUES(shop_name);
