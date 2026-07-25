-- ============================================================
-- Inventory Management System — MySQL Schema Script
-- Database : inventory_db
-- Engine   : InnoDB | Charset: utf8mb4
-- ============================================================

CREATE DATABASE IF NOT EXISTS inventory_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE inventory_db;

-- ============================================================
-- 1. SUPPLIER MASTER
-- Stores all supplier / vendor information
-- ============================================================
CREATE TABLE IF NOT EXISTS supplier_master (
    supplier_id     BIGINT          NOT NULL AUTO_INCREMENT,
    supplier_code   VARCHAR(20)     NOT NULL UNIQUE COMMENT 'Unique supplier code e.g. SUP001',
    supplier_name   VARCHAR(100)    NOT NULL,
    address_line1   VARCHAR(200),
    address_line2   VARCHAR(200),
    city            VARCHAR(50),
    pincode         VARCHAR(10),
    type            VARCHAR(30)     COMMENT 'LOCAL / INTERSTATE / IMPORT',
    contact_person  VARCHAR(100),
    phone_no        VARCHAR(15),
    email_id        VARCHAR(100),
    gst_no          VARCHAR(20),
    status          VARCHAR(10)     NOT NULL DEFAULT 'ACTIVE' COMMENT 'ACTIVE / INACTIVE',
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_supplier PRIMARY KEY (supplier_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Supplier / Vendor Master';

-- ============================================================
-- 2. CUSTOMER MASTER
-- Stores all customer / buyer information
-- ============================================================
CREATE TABLE IF NOT EXISTS customer_master (
    customer_id       BIGINT          NOT NULL AUTO_INCREMENT,
    customer_code     VARCHAR(20)     NOT NULL UNIQUE COMMENT 'Unique customer code e.g. CUST001',
    customer_name     VARCHAR(100)    NOT NULL,
    address_line1     VARCHAR(200),
    address_line2     VARCHAR(200),
    city              VARCHAR(50),
    pincode           VARCHAR(10),
    contact_person    VARCHAR(100),
    phone_no          VARCHAR(15),
    email_id          VARCHAR(100),
    gst_no            VARCHAR(20),
    credit_limit      DECIMAL(15,2)   DEFAULT 0.00,
    opening_balance   DECIMAL(15,2)   DEFAULT 0.00,
    status            VARCHAR(10)     NOT NULL DEFAULT 'ACTIVE',
    created_at        TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_customer PRIMARY KEY (customer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Customer / Buyer Master';

-- ============================================================
-- 3. ITEM MASTER
-- Stores products / inventory items
-- ============================================================
CREATE TABLE IF NOT EXISTS item_master (
    item_id           BIGINT          NOT NULL AUTO_INCREMENT,
    item_code         VARCHAR(20)     NOT NULL UNIQUE COMMENT 'Unique item code e.g. ITEM001',
    item_name         VARCHAR(100)    NOT NULL,
    description       VARCHAR(255),
    category          VARCHAR(50),
    unit              VARCHAR(20)     COMMENT 'KG / PCS / BOX / LTR',
    purchase_rate     DECIMAL(12,2)   DEFAULT 0.00,
    selling_rate      DECIMAL(12,2)   DEFAULT 0.00,
    gst_percent       DECIMAL(5,2)    DEFAULT 0.00,
    opening_stock     DECIMAL(12,3)   DEFAULT 0.000,
    reorder_level     DECIMAL(12,3)   DEFAULT 0.000,
    status            VARCHAR(10)     NOT NULL DEFAULT 'ACTIVE',
    created_at        TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_item PRIMARY KEY (item_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Item / Product Master';

-- ============================================================
-- 4. BANK MASTER
-- Stores company bank accounts
-- ============================================================
CREATE TABLE IF NOT EXISTS bank_master (
    bank_id           BIGINT          NOT NULL AUTO_INCREMENT,
    bank_code         VARCHAR(20)     NOT NULL UNIQUE,
    bank_name         VARCHAR(100)    NOT NULL,
    account_no        VARCHAR(30)     NOT NULL UNIQUE,
    account_type      VARCHAR(20)     COMMENT 'CURRENT / SAVINGS',
    ifsc_code         VARCHAR(15),
    branch_name       VARCHAR(100),
    opening_balance   DECIMAL(15,2)   DEFAULT 0.00,
    status            VARCHAR(10)     NOT NULL DEFAULT 'ACTIVE',
    created_at        TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_bank PRIMARY KEY (bank_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Bank Account Master';

-- ============================================================
-- 5a. BILLING MASTER (Header)
-- One record per bill / sales invoice
-- ============================================================
CREATE TABLE IF NOT EXISTS billing_master (
    billing_id        BIGINT          NOT NULL AUTO_INCREMENT,
    bill_no           VARCHAR(20)     NOT NULL UNIQUE COMMENT 'e.g. BILL-2026-001',
    bill_date         DATE            NOT NULL,
    customer_id       BIGINT          NOT NULL,
    total_amount      DECIMAL(15,2)   NOT NULL DEFAULT 0.00,
    discount_amount   DECIMAL(15,2)   DEFAULT 0.00,
    tax_amount        DECIMAL(15,2)   DEFAULT 0.00,
    net_amount        DECIMAL(15,2)   NOT NULL DEFAULT 0.00,
    payment_status    VARCHAR(20)     DEFAULT 'PENDING' COMMENT 'PENDING / PARTIAL / PAID',
    remarks           VARCHAR(255),
    created_at        TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_billing     PRIMARY KEY (billing_id),
    CONSTRAINT fk_bill_cust   FOREIGN KEY (customer_id) REFERENCES customer_master(customer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Sales Bill / Invoice Header';

-- ============================================================
-- 5b. BILLING DETAIL (Line Items)
-- One record per line item of a bill
-- ============================================================
CREATE TABLE IF NOT EXISTS billing_detail (
    detail_id         BIGINT          NOT NULL AUTO_INCREMENT,
    billing_id        BIGINT          NOT NULL,
    item_id           BIGINT          NOT NULL,
    quantity          DECIMAL(12,3)   NOT NULL,
    unit_price        DECIMAL(12,2)   NOT NULL,
    discount_percent  DECIMAL(5,2)    DEFAULT 0.00,
    gst_percent       DECIMAL(5,2)    DEFAULT 0.00,
    line_total        DECIMAL(15,2)   NOT NULL,
    CONSTRAINT pk_billing_detail    PRIMARY KEY (detail_id),
    CONSTRAINT fk_detail_billing    FOREIGN KEY (billing_id) REFERENCES billing_master(billing_id) ON DELETE CASCADE,
    CONSTRAINT fk_detail_item       FOREIGN KEY (item_id)    REFERENCES item_master(item_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Sales Bill Line Items';

-- ============================================================
-- 6a. RECEIPT MASTER (Header)
-- One record per payment receipt from a customer
-- ============================================================
CREATE TABLE IF NOT EXISTS receipt_master (
    receipt_id        BIGINT          NOT NULL AUTO_INCREMENT,
    receipt_no        VARCHAR(20)     NOT NULL UNIQUE COMMENT 'e.g. REC-2026-001',
    receipt_date      DATE            NOT NULL,
    customer_id       BIGINT          NOT NULL,
    bank_id           BIGINT,
    payment_mode      VARCHAR(20)     COMMENT 'CASH / CHEQUE / NEFT / UPI',
    cheque_no         VARCHAR(20),
    cheque_date       DATE,
    total_amount      DECIMAL(15,2)   NOT NULL DEFAULT 0.00,
    remarks           VARCHAR(255),
    created_at        TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_receipt       PRIMARY KEY (receipt_id),
    CONSTRAINT fk_rec_cust      FOREIGN KEY (customer_id) REFERENCES customer_master(customer_id),
    CONSTRAINT fk_rec_bank      FOREIGN KEY (bank_id)     REFERENCES bank_master(bank_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Customer Payment Receipt Header';

-- ============================================================
-- 6b. RECEIPT DETAIL
-- Allocates receipt amount to specific bills
-- ============================================================
CREATE TABLE IF NOT EXISTS receipt_detail (
    rec_detail_id     BIGINT          NOT NULL AUTO_INCREMENT,
    receipt_id        BIGINT          NOT NULL,
    billing_id        BIGINT          NOT NULL,
    allocated_amount  DECIMAL(15,2)   NOT NULL,
    CONSTRAINT pk_receipt_detail    PRIMARY KEY (rec_detail_id),
    CONSTRAINT fk_recdet_receipt    FOREIGN KEY (receipt_id)  REFERENCES receipt_master(receipt_id) ON DELETE CASCADE,
    CONSTRAINT fk_recdet_billing    FOREIGN KEY (billing_id)  REFERENCES billing_master(billing_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Receipt to Bill Allocation';

-- ============================================================
-- END OF SCHEMA
-- ============================================================
