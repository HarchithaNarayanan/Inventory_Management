USE inventory_db;

INSERT INTO company_master (
    company_code, company_name, address_line1, address_line2, city, pincode,
    phone_no, email_id, website, gst_no, financial_year, logo_url,
    status, is_active, created_at, updated_at
) VALUES 
(
    'CMP001', 'TechMart Solutions Pvt Ltd', 'No. 120, OMR Road', 'Sholinganallur', 'Chennai', '600119',
    '044-45678901', 'info@techmartsolutions.com', 'www.techmartsolutions.com', '33AABCT1234F1Z5', '2025-2026', 'https://www.techmartsolutions.com/logo.png',
    'ACTIVE', 1, NOW(), NOW()
);
