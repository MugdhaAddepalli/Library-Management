CREATE DATABASE library_db;
USE library_db;

-- =========================
-- BOOKS TABLE
-- =========================
CREATE TABLE books (
    id INT PRIMARY KEY,
    title VARCHAR(100),
    author VARCHAR(100),
    available BOOLEAN
);

-- =========================
-- USERS TABLE
-- =========================
CREATE TABLE users (
    user_id INT PRIMARY KEY,
    name VARCHAR(100),
    email VARCHAR(100),
    phone VARCHAR(20)
);

-- =========================
-- TRANSACTIONS TABLE
-- =========================
CREATE TABLE transactions (
    id INT PRIMARY KEY AUTO_INCREMENT,
    book_id INT,
    user_id INT,
    issue_date DATE,
    due_date DATE,
    return_date DATE,

    FOREIGN KEY (book_id) REFERENCES books(id),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);
