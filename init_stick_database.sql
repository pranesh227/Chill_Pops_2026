USE chillpops;

-- 1. Recreate shop_settings cleanly
DROP TABLE IF EXISTS shop_settings;
CREATE TABLE shop_settings (
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
VALUES (1, 'Chill Pops', 'chillpops@oksbi', 'Chill Pops Franchise', 5.00, FALSE, '123 Popsicle Street, Chill Zone, Bengaluru, Karnataka 560001', '+91 98765 43210', '/images/logo.svg', 'Thank you for choosing Chill Pops! Cool your day the Chill Pops way!');

-- 2. Clear out existing non-stick categories and products safely
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE transaction_items;
TRUNCATE TABLE payment_details;
TRUNCATE TABLE transactions;
TRUNCATE TABLE products;
TRUNCATE TABLE categories;
SET FOREIGN_KEY_CHECKS = 1;

-- 3. Insert Stick Ice Cream Categories
INSERT INTO categories (id, name) VALUES
(1, 'Fruit Pops'),
(2, 'Choco Sticks'),
(3, 'Cream Sticks'),
(4, 'Exotic & Premium Sticks');

-- 4. Insert Strict Stick Ice Cream Products (Prices strictly between ₹10 and ₹50)
-- Both .jpg and fallback .svg are present in /images/products/
INSERT INTO products (id, name, description, category_id, price, image_path, available, stock, deleted) VALUES
(1, 'Classic Mango Pop', 'Juicy natural Alphonso mango popsicle with a cool tropical blast.', 1, 10.00, '/images/products/pop_mango.jpg', 1, 100, 0),
(2, 'Orange Splash Pop', 'Refreshing zesty sun-kissed orange citrus stick ice cream.', 1, 10.00, '/images/products/pop_orange.jpg', 1, 100, 0),
(3, 'Strawberry Chill Pop', 'Sweet farm-fresh real strawberry fruit popsicle with berry chunks.', 1, 20.00, '/images/products/pop_strawberry.jpg', 1, 100, 0),
(4, 'Pineapple Twist Pop', 'Tangy sweet tropical pineapple ice bar with cool mint undertones.', 1, 20.00, '/images/products/pop_pineapple.jpg', 1, 100, 0),
(5, 'Rose Milk Pop', 'Aromatic fragrant rose milk cream stick infused with cardamom.', 3, 20.00, '/images/products/pop_rosemilk.jpg', 1, 100, 0),
(6, 'Watermelon Breeze Pop', 'Crisp hydrating summer watermelon popsicle with a lime twist.', 1, 20.00, '/images/products/pop_watermelon.jpg', 1, 100, 0),
(7, 'Tangy Kala Khatta Stick', 'Desi style spiced sweet and tangy blackberry ice pop stick.', 1, 15.00, '/images/products/pop_kalakhatta.jpg', 1, 100, 0),
(8, 'Blueberry Blast Pop', 'Wild mountain blueberry popsicle bursting with rich antioxidant flavor.', 1, 30.00, '/images/products/pop_blueberry.jpg', 1, 100, 0),
(9, 'Chocolate Crunch Stick', 'Velvety milk chocolate ice stick coated in crispy waffle crunch.', 2, 30.00, '/images/products/pop_chocolate.jpg', 1, 100, 0),
(10, 'Blackcurrant Freeze Pop', 'Deep purple sweet and sour blackcurrant stick for an icy kick.', 1, 30.00, '/images/products/pop_blackcurrant.jpg', 1, 100, 0),
(11, 'Butterscotch Delight Stick', 'Rich caramelized butterscotch ice cream stick with crunchy praline nuts.', 3, 40.00, '/images/products/pop_butterscotch.jpg', 1, 100, 0),
(12, 'Belgian Chocolate Stick', 'Decadent 70% dark Belgian cocoa stick ice cream with a glossy fudge dip.', 2, 40.00, '/images/products/pop_belgian_choc.jpg', 1, 100, 0),
(13, 'Mixed Berry Blast Pop', 'A trio of raspberry, blueberry, and cranberry in a frosted stick.', 1, 40.00, '/images/products/pop_mixed_berry.jpg', 1, 100, 0),
(14, 'Malai Kulfi Stick', 'Authentic slow-simmered rich creamy rabdi kulfi stick on traditional wood.', 3, 35.00, '/images/products/pop_malai_kulfi.jpg', 1, 100, 0),
(15, 'Roasted Almond Stick', 'Nutty roasted California almond bits studded over silky cream stick.', 4, 45.00, '/images/products/pop_roasted_almond.jpg', 1, 100, 0),
(16, 'Premium Mango Cream Stick', 'Luscious Alphonso mango pulp blended with fresh dairy cream on a stick.', 4, 50.00, '/images/products/pop_mango_cream.jpg', 1, 100, 0),
(17, 'Double Chocolate Chill Stick', 'Double layered intense dark and milk chocolate gelato stick.', 2, 50.00, '/images/products/pop_double_choc.jpg', 1, 100, 0),
(18, 'Kesar Badam Stick', 'Royal saffron threads and crushed Kashmiri badam in frozen cream.', 4, 50.00, '/images/products/pop_kesar_badam.jpg', 1, 100, 0);

-- 5. Ensure Admin User exists (BCrypt for 'admin123')
INSERT INTO users (id, username, password_hash, role, enabled)
VALUES (1, 'admin', '$2a$10$5KTEWOTFRXSrAwIQNPcapuOm.7i7CKsGGmkbQb/UEbO5MnzhqGrae', 'OWNER', 1)
ON DUPLICATE KEY UPDATE password_hash = '$2a$10$5KTEWOTFRXSrAwIQNPcapuOm.7i7CKsGGmkbQb/UEbO5MnzhqGrae', role = 'OWNER', enabled = 1;
