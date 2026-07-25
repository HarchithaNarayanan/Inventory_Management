USE inventory_db;

INSERT INTO bank_master (
    bank_code, bank_name, bank_branch, account_no, limit_amount, gl_code, 
    account_type, opening_balance, status, created_at, updated_at
) VALUES 
(
    'BNK001', 'State Bank of India', 'Chennai Main Branch', '123456789012', 500000, 'SBI001',
    'CURRENT', 0, 'ACTIVE', NOW(), NOW()
),
(
    'BNK002', 'HDFC Bank', 'Velachery Branch', '987654321098', 1000000, 'HDFC001',
    'CURRENT', 0, 'ACTIVE', NOW(), NOW()
),
(
    'BNK003', 'ICICI Bank', 'Tambaram Branch', '567890123456', 750000, 'ICICI001',
    'CURRENT', 0, 'ACTIVE', NOW(), NOW()
);
