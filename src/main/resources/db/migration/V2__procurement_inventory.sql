-- Procurement Module
CREATE TABLE suppliers (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    name VARCHAR(255) NOT NULL,
    contact_email VARCHAR(255),
    address TEXT
);

CREATE TABLE items (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(19, 2),
    uom VARCHAR(20) -- Unit of Measure (EA, KG, M)
);

CREATE TABLE purchase_orders (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    supplier_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL, -- DRAFT, APPROVED, SENT, CLOSED, CANCELLED
    total_amount DECIMAL(19, 2),
    CONSTRAINT fk_po_supplier FOREIGN KEY (supplier_id) REFERENCES suppliers (id)
);

CREATE TABLE purchase_order_items (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    purchase_order_id BIGINT NOT NULL,
    item_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL,
    price DECIMAL(19, 2) NOT NULL,
    CONSTRAINT fk_po_items_po FOREIGN KEY (purchase_order_id) REFERENCES purchase_orders (id),
    CONSTRAINT fk_po_items_item FOREIGN KEY (item_id) REFERENCES items (id)
);

-- Inventory Module
CREATE TABLE warehouses (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    name VARCHAR(255) NOT NULL UNIQUE,
    location VARCHAR(255)
);

CREATE TABLE stocks (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    item_id BIGINT NOT NULL,
    warehouse_id BIGINT NOT NULL,
    on_hand INTEGER NOT NULL DEFAULT 0,
    version BIGINT, -- For Optimistic Locking
    CONSTRAINT fk_stock_item FOREIGN KEY (item_id) REFERENCES items (id),
    CONSTRAINT fk_stock_warehouse FOREIGN KEY (warehouse_id) REFERENCES warehouses (id),
    CONSTRAINT uq_stock_item_warehouse UNIQUE (item_id, warehouse_id)
);

CREATE TABLE inventory_transactions (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    item_id BIGINT NOT NULL,
    warehouse_id BIGINT NOT NULL,
    type VARCHAR(20) NOT NULL, -- IN, OUT
    quantity INTEGER NOT NULL,
    reference_id BIGINT, -- e.g., PO ID or Work Order ID
    reference_type VARCHAR(50), -- PO, WO
    CONSTRAINT fk_inv_trans_item FOREIGN KEY (item_id) REFERENCES items (id),
    CONSTRAINT fk_inv_trans_warehouse FOREIGN KEY (warehouse_id) REFERENCES warehouses (id)
);

-- Initial Data
INSERT INTO warehouses (created_at, updated_at, name, location) VALUES (NOW(), NOW(), 'Main Warehouse', 'HQ');
INSERT INTO suppliers (created_at, updated_at, name, contact_email) VALUES (NOW(), NOW(), 'Acme Corp', 'contact@acme.com');
INSERT INTO items (created_at, updated_at, code, name, price, uom) VALUES (NOW(), NOW(), 'ITEM-001', 'Steel Sheet', 100.00, 'EA');
INSERT INTO items (created_at, updated_at, code, name, price, uom) VALUES (NOW(), NOW(), 'ITEM-002', 'Bolt M10', 0.50, 'EA');
