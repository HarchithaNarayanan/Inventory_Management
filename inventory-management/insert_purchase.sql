USE inventory_db;

-- 1. Insert Supplier
INSERT INTO supplier_master (supplier_code, supplier_name, status, is_active, created_at, updated_at) 
VALUES ('SUP001', 'ABC Traders', 'ACTIVE', 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE supplier_name = 'ABC Traders';

-- 2. Insert Purchase Header
-- Get Supplier ID
SET @sup_id = (SELECT supplier_id FROM supplier_master WHERE supplier_code = 'SUP001');

INSERT INTO purchase_master (
    bill_no, bill_date, supplier_id, total_amount, tax_amount, net_amount, remarks, created_at, updated_at
) VALUES (
    'PUR001', '2026-06-23', @sup_id, 242500, 43650, 286150, 'Initial stock purchase', NOW(), NOW()
);

-- Get Purchase ID
SET @pur_id = (SELECT purchase_id FROM purchase_master WHERE bill_no = 'PUR001');

-- 3. Insert Purchase Details
-- Item 1: ITM001
SET @itm1 = (SELECT item_id FROM item_master WHERE item_code = 'ITM001');
INSERT INTO purchase_detail (purchase_id, item_id, quantity, unit_price, gst_percent, line_total)
VALUES (@pur_id, @itm1, 5, 45000, 18, 225000);

-- Item 2: ITM002
SET @itm2 = (SELECT item_id FROM item_master WHERE item_code = 'ITM002');
INSERT INTO purchase_detail (purchase_id, item_id, quantity, unit_price, gst_percent, line_total)
VALUES (@pur_id, @itm2, 20, 500, 18, 10000);

-- Item 3: ITM003
SET @itm3 = (SELECT item_id FROM item_master WHERE item_code = 'ITM003');
INSERT INTO purchase_detail (purchase_id, item_id, quantity, unit_price, gst_percent, line_total)
VALUES (@pur_id, @itm3, 30, 250, 18, 7500);
