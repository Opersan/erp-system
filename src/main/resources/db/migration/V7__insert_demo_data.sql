-- Insert Suppliers
INSERT INTO suppliers (created_at, updated_at, name, contact_email, address)
SELECT NOW(), NOW(), 'TechComponents Ltd', 'sales@techcomponents.com', '123 Tech Park, Silicon Valley'
WHERE NOT EXISTS (SELECT 1 FROM suppliers WHERE name = 'TechComponents Ltd');

INSERT INTO suppliers (created_at, updated_at, name, contact_email, address)
SELECT NOW(), NOW(), 'MetalWorks Inc', 'orders@metalworks.com', '456 Industrial Ave, Detroit'
WHERE NOT EXISTS (SELECT 1 FROM suppliers WHERE name = 'MetalWorks Inc');

INSERT INTO suppliers (created_at, updated_at, name, contact_email, address)
SELECT NOW(), NOW(), 'Global Logistics', 'contact@globallogistics.com', '789 Harbor Rd, Seattle'
WHERE NOT EXISTS (SELECT 1 FROM suppliers WHERE name = 'Global Logistics');

-- Insert Warehouses
INSERT INTO warehouses (created_at, updated_at, name, location) VALUES
(NOW(), NOW(), 'Main Warehouse', 'New York'),
(NOW(), NOW(), 'West Coast Hub', 'Los Angeles'),
(NOW(), NOW(), 'European Distribution', 'Berlin')
ON CONFLICT (name) DO NOTHING;

-- Insert Items
INSERT INTO items (created_at, updated_at, code, name, description, price, uom) VALUES
(NOW(), NOW(), 'ITEM-001', 'Steel Sheet', 'Standard steel sheet 2mm', 50.00, 'SHEET'),
(NOW(), NOW(), 'ITEM-002', 'Electronic Circuit Board', 'Main control board', 120.00, 'UNIT'),
(NOW(), NOW(), 'ITEM-003', 'Plastic Casing', 'Durable plastic casing', 15.00, 'UNIT'),
(NOW(), NOW(), 'PROD-001', 'Smart Widget', 'Finished smart widget product', 350.00, 'UNIT')
ON CONFLICT (code) DO NOTHING;

-- Insert BOMs (Bill of Materials) for PROD-001
INSERT INTO boms (created_at, updated_at, item_id, name, description) 
SELECT NOW(), NOW(), id, 'BOM-PROD-001', 'Standard BOM for Smart Widget'
FROM items 
WHERE code = 'PROD-001'
AND NOT EXISTS (SELECT 1 FROM boms WHERE name = 'BOM-PROD-001');

-- Insert BOM Components
INSERT INTO bom_components (created_at, updated_at, bom_id, item_id, quantity)
SELECT NOW(), NOW(), b.id, i.id, 1
FROM boms b, items i
WHERE b.name = 'BOM-PROD-001' AND i.code = 'ITEM-002'
AND NOT EXISTS (SELECT 1 FROM bom_components bc WHERE bc.bom_id = b.id AND bc.item_id = i.id);

INSERT INTO bom_components (created_at, updated_at, bom_id, item_id, quantity)
SELECT NOW(), NOW(), b.id, i.id, 1
FROM boms b, items i
WHERE b.name = 'BOM-PROD-001' AND i.code = 'ITEM-003'
AND NOT EXISTS (SELECT 1 FROM bom_components bc WHERE bc.bom_id = b.id AND bc.item_id = i.id);

INSERT INTO bom_components (created_at, updated_at, bom_id, item_id, quantity)
SELECT NOW(), NOW(), b.id, i.id, 2
FROM boms b, items i
WHERE b.name = 'BOM-PROD-001' AND i.code = 'ITEM-001'
AND NOT EXISTS (SELECT 1 FROM bom_components bc WHERE bc.bom_id = b.id AND bc.item_id = i.id);
