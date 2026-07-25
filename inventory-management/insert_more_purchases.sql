USE inventory_db;

-- 1. Insert New Suppliers
INSERT INTO supplier_master (supplier_code, supplier_name, status, is_active, created_at, updated_at) 
VALUES ('SUP002', 'Sri Lakshmi Suppliers', 'ACTIVE', 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE supplier_name = 'Sri Lakshmi Suppliers';

INSERT INTO supplier_master (supplier_code, supplier_name, status, is_active, created_at, updated_at) 
VALUES ('SUP003', 'National Distributors', 'ACTIVE', 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE supplier_name = 'National Distributors';

-- -----------------------------------------------------------------------------
-- Purchase 2
-- -----------------------------------------------------------------------------
SET @sup_id2 = (SELECT supplier_id FROM supplier_master WHERE supplier_code = 'SUP002');

INSERT INTO purchase_master (
    bill_no, bill_date, supplier_id, total_amount, tax_amount, net_amount, remarks, created_at, updated_at
) VALUES (
    'PUR002', '2026-06-21', @sup_id2, 116000, 20880, 136880, 'Restocking monitors and printers', NOW(), NOW()
);

SET @pur_id2 = (SELECT purchase_id FROM purchase_master WHERE bill_no = 'PUR002');

-- Item: Samsung Monitor (ITM005)
SET @itm5 = (SELECT item_id FROM item_master WHERE item_code = 'ITM005');
INSERT INTO purchase_detail (purchase_id, item_id, quantity, unit_price, gst_percent, line_total)
VALUES (@pur_id2, @itm5, 10, 8000, 18, 80000);

-- Item: HP Printer (ITM004)
SET @itm4 = (SELECT item_id FROM item_master WHERE item_code = 'ITM004');
INSERT INTO purchase_detail (purchase_id, item_id, quantity, unit_price, gst_percent, line_total)
VALUES (@pur_id2, @itm4, 3, 12000, 18, 36000);


-- -----------------------------------------------------------------------------
-- Purchase 3
-- -----------------------------------------------------------------------------
SET @sup_id3 = (SELECT supplier_id FROM supplier_master WHERE supplier_code = 'SUP003');

INSERT INTO purchase_master (
    bill_no, bill_date, supplier_id, total_amount, tax_amount, net_amount, remarks, created_at, updated_at
) VALUES (
    'PUR003', '2026-06-22', @sup_id3, 14550, 2619, 17169, 'Accessories restock', NOW(), NOW()
);

SET @pur_id3 = (SELECT purchase_id FROM purchase_master WHERE bill_no = 'PUR003');

-- Item: Keyboard (ITM002)
SET @itm2 = (SELECT item_id FROM item_master WHERE item_code = 'ITM002');
INSERT INTO purchase_detail (purchase_id, item_id, quantity, unit_price, gst_percent, line_total)
VALUES (@pur_id3, @itm2, 15, 520, 18, 7800);

-- Item: Mouse (ITM003)
SET @itm3 = (SELECT item_id FROM item_master WHERE item_code = 'ITM003');
INSERT INTO purchase_detail (purchase_id, item_id, quantity, unit_price, gst_percent, line_total)
VALUES (@pur_id3, @itm3, 25, 270, 18, 6750);
