-- create_inventory_db.sql
-- This script creates the inventory database table called "products"

PRAGMA foreign_keys = ON;

CREATE TABLE IF NOT EXISTS products (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    sku TEXT NOT NULL UNIQUE,
    name TEXT NOT NULL,
    category TEXT,
    price REAL NOT NULL CHECK(price >= 0),
    quantity INTEGER NOT NULL CHECK(quantity >= 0),
    supplier TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);
