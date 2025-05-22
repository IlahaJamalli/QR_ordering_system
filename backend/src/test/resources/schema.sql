-- Create tables for testing
DROP TABLE IF EXISTS menu_items;
CREATE TABLE menu_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    category VARCHAR(255),
    image_url VARCHAR(255)
);

DROP TABLE IF EXISTS tables;
CREATE TABLE tables (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    table_number VARCHAR(50) NOT NULL UNIQUE,
    qr_code VARCHAR(255),
    status VARCHAR(50) DEFAULT 'AVAILABLE'
);

DROP TABLE IF EXISTS orders;
CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    table_number VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    ordered_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    comments_history TEXT
); 