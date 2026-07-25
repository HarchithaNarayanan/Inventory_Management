USE inventory_db;

INSERT INTO item_master (
    item_code, item_name, description, category, unit_of_measure, unit_of_rate, grade,
    purchase_gl, sales_gl, entry_id, entered_by, modified_id, modified_by,
    manufacture_date, expiry_date, entry_date, purchase_rate, selling_rate,
    gst_percent, opening_stock, reorder_level, status, is_active, created_at, updated_at
) VALUES 
(
    'ITM001', 'Dell Inspiron 15 Laptop', 'Intel i5, 16GB RAM, 512GB SSD', 'Electronics', 'Nos', 'Piece', 'A',
    'Purchase Account', 'Sales Account', '1', 'Hana', '1', 'Hana',
    '2025-01-01 00:00:00', '2030-01-01 00:00:00', NOW(), 45000, 52000,
    18, 10, 2, 'ACTIVE', 1, NOW(), NOW()
),
(
    'ITM002', 'Logitech Keyboard', 'USB Wired Keyboard', 'Accessories', 'Nos', 'Piece', 'A',
    'Purchase Account', 'Sales Account', '2', 'Hana', '2', 'Hana',
    '2025-03-01 00:00:00', '2030-03-01 00:00:00', NOW(), 500, 750,
    18, 50, 10, 'ACTIVE', 1, NOW(), NOW()
),
(
    'ITM003', 'HP Optical Mouse', 'USB Optical Mouse', 'Accessories', 'Nos', 'Piece', 'A',
    'Purchase Account', 'Sales Account', '3', 'Hana', '3', 'Hana',
    '2025-02-01 00:00:00', '2030-02-01 00:00:00', NOW(), 250, 400,
    18, 100, 20, 'ACTIVE', 1, NOW(), NOW()
),
(
    'ITM004', 'HP LaserJet Printer', 'Wireless Laser Printer', 'Electronics', 'Nos', 'Piece', 'A',
    'Purchase Account', 'Sales Account', '4', 'Hana', '4', 'Hana',
    '2025-04-01 00:00:00', '2030-04-01 00:00:00', NOW(), 12000, 14500,
    18, 8, 2, 'ACTIVE', 1, NOW(), NOW()
),
(
    'ITM005', 'Samsung 24-inch Monitor', 'Full HD LED Monitor', 'Electronics', 'Nos', 'Piece', 'A',
    'Purchase Account', 'Sales Account', '5', 'Hana', '5', 'Hana',
    '2025-05-01 00:00:00', '2030-05-01 00:00:00', NOW(), 8000, 9500,
    18, 15, 3, 'ACTIVE', 1, NOW(), NOW()
);
