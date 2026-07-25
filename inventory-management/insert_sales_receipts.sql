USE inventory_db;

-- 1. Insert New Customers
INSERT INTO customer_master (customer_code, customer_name, status, is_active, created_at, updated_at) 
VALUES ('CUST001', 'Chennai Tech Solutions', 'ACTIVE', 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE customer_name = 'Chennai Tech Solutions';

INSERT INTO customer_master (customer_code, customer_name, status, is_active, created_at, updated_at) 
VALUES ('CUST002', 'Velammal IT Services', 'ACTIVE', 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE customer_name = 'Velammal IT Services';

-- -----------------------------------------------------------------------------
-- Sales Bill 1: SAL001
-- -----------------------------------------------------------------------------
SET @cust_id1 = (SELECT customer_id FROM customer_master WHERE customer_code = 'CUST001');

INSERT INTO billing_master (
    bill_no, bill_date, customer_id, total_amount, discount_amount, tax_amount, net_amount, payment_status, remarks, created_at, updated_at
) VALUES (
    'SAL001', '2026-06-23', @cust_id1, 113500.00, 5500.00, 19440.00, 127440.00, 'PARTIAL', 'Corporate order', NOW(), NOW()
);

SET @bill_id1 = (SELECT billing_id FROM billing_master WHERE bill_no = 'SAL001');

-- Item 1: Dell Laptop (ITM001)
SET @itm1 = (SELECT item_id FROM item_master WHERE item_code = 'ITM001');
INSERT INTO billing_detail (billing_id, item_id, quantity, unit_price, discount_percent, gst_percent, line_total)
VALUES (@bill_id1, @itm1, 2, 55000, 5, 18, 123310.00);

-- Item 2: Keyboard (ITM002)
SET @itm2 = (SELECT item_id FROM item_master WHERE item_code = 'ITM002');
INSERT INTO billing_detail (billing_id, item_id, quantity, unit_price, discount_percent, gst_percent, line_total)
VALUES (@bill_id1, @itm2, 5, 700, 0, 18, 4130.00);

-- -----------------------------------------------------------------------------
-- Sales Bill 2: SAL002
-- -----------------------------------------------------------------------------
SET @cust_id2 = (SELECT customer_id FROM customer_master WHERE customer_code = 'CUST002');

INSERT INTO billing_master (
    bill_no, bill_date, customer_id, total_amount, discount_amount, tax_amount, net_amount, payment_status, remarks, created_at, updated_at
) VALUES (
    'SAL002', '2026-06-23', @cust_id2, 32000.00, 570.00, 5657.40, 37087.40, 'PENDING', 'Office accessories', NOW(), NOW()
);

SET @bill_id2 = (SELECT billing_id FROM billing_master WHERE bill_no = 'SAL002');

-- Item 1: Samsung Monitor (ITM005)
SET @itm5 = (SELECT item_id FROM item_master WHERE item_code = 'ITM005');
INSERT INTO billing_detail (billing_id, item_id, quantity, unit_price, discount_percent, gst_percent, line_total)
VALUES (@bill_id2, @itm5, 3, 9500, 2, 18, 32957.40);

-- Item 2: Mouse (ITM003)
SET @itm3 = (SELECT item_id FROM item_master WHERE item_code = 'ITM003');
INSERT INTO billing_detail (billing_id, item_id, quantity, unit_price, discount_percent, gst_percent, line_total)
VALUES (@bill_id2, @itm3, 10, 350, 0, 18, 4130.00);

-- -----------------------------------------------------------------------------
-- Account Transactions (Receipts)
-- -----------------------------------------------------------------------------
-- Get a bank account ID (assuming BNK002 HDFC exists from previous step)
SET @bank_id = (SELECT bank_id FROM bank_master WHERE bank_code = 'BNK002');

-- Transaction 1
INSERT INTO account_transaction (
    transaction_date, transaction_type, reference_no, bank_id, amount, dr_cr, remarks, created_at, updated_at
) VALUES (
    '2026-06-23', 'RECEIPT', 'UPI123456', @bank_id, 115000.00, 'CR', 'Partial payment against SAL001 - Chennai Tech Solutions (UPI)', NOW(), NOW()
);

-- Transaction 2
INSERT INTO account_transaction (
    transaction_date, transaction_type, reference_no, bank_id, amount, dr_cr, remarks, created_at, updated_at
) VALUES (
    '2026-06-23', 'RECEIPT', 'NEFT987654', @bank_id, 32000.00, 'CR', 'Payment for SAL002 - Velammal IT Services (Bank Transfer)', NOW(), NOW()
);
