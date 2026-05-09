CREATE DATABASE IF NOT EXISTS atm_db;
USE atm_db;

-- Table to store user account information
CREATE TABLE IF NOT EXISTS users (
    account_number VARCHAR(20) PRIMARY KEY,
    user_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    pin VARCHAR(255) NOT NULL,
    balance DECIMAL(15, 2) DEFAULT 0.00,
    failed_attempts INT DEFAULT 0,
    account_locked BOOLEAN DEFAULT FALSE
);

-- Table to store transaction history
CREATE TABLE IF NOT EXISTS transactions (
    transaction_id INT AUTO_INCREMENT PRIMARY KEY,
    account_number VARCHAR(20) NOT NULL,
    transaction_type VARCHAR(50) NOT NULL,
    amount DECIMAL(15, 2) NOT NULL,
    transaction_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (account_number) REFERENCES users(account_number) ON DELETE CASCADE
);

-- Sample Data for Testing (PIN is 1234)
INSERT INTO users (account_number, user_name, email, pin, balance) 
VALUES ('1234567890', 'John Doe', 'john@example.com', '$2a$10$jS6SufS6YfW7.p4F3S9LDe8.U5mK/5eP5G6p7/yv7p.hO0S2F8S/S', 5000.00)
ON DUPLICATE KEY UPDATE pin='$2a$10$jS6SufS6YfW7.p4F3S9LDe8.U5mK/5eP5G6p7/yv7p.hO0S2F8S/S';
