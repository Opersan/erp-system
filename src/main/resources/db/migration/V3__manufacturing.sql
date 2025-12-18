-- Manufacturing Module

CREATE TABLE boms (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    item_id BIGINT NOT NULL, -- Finished Good
    name VARCHAR(255) NOT NULL,
    description TEXT,
    base_quantity DECIMAL(19, 2) NOT NULL DEFAULT 1.0,
    CONSTRAINT fk_bom_item FOREIGN KEY (item_id) REFERENCES items (id)
);

CREATE TABLE bom_components (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    bom_id BIGINT NOT NULL,
    item_id BIGINT NOT NULL, -- Component
    quantity DECIMAL(19, 2) NOT NULL, -- Quantity per base_quantity
    CONSTRAINT fk_bom_comp_bom FOREIGN KEY (bom_id) REFERENCES boms (id),
    CONSTRAINT fk_bom_comp_item FOREIGN KEY (item_id) REFERENCES items (id)
);

CREATE TABLE work_orders (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    item_id BIGINT NOT NULL,
    quantity DECIMAL(19, 2) NOT NULL,
    status VARCHAR(20) NOT NULL, -- PLANNED, RELEASED, IN_PROGRESS, COMPLETED, CLOSED, CANCELLED
    start_date TIMESTAMP,
    end_date TIMESTAMP,
    CONSTRAINT fk_wo_item FOREIGN KEY (item_id) REFERENCES items (id)
);

CREATE TABLE work_order_requirements (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    work_order_id BIGINT NOT NULL,
    item_id BIGINT NOT NULL,
    required_quantity DECIMAL(19, 2) NOT NULL,
    allocated_quantity DECIMAL(19, 2) DEFAULT 0,
    issued_quantity DECIMAL(19, 2) DEFAULT 0,
    CONSTRAINT fk_wo_req_wo FOREIGN KEY (work_order_id) REFERENCES work_orders (id),
    CONSTRAINT fk_wo_req_item FOREIGN KEY (item_id) REFERENCES items (id)
);

-- Initial Data
-- Let's assume ITEM-003 is a Finished Good "Steel Box" made of ITEM-001 (Sheet) and ITEM-002 (Bolt)
INSERT INTO items (created_at, updated_at, code, name, price, uom) VALUES (NOW(), NOW(), 'ITEM-003', 'Steel Box', 250.00, 'EA');

INSERT INTO boms (created_at, updated_at, item_id, name, base_quantity) 
VALUES (NOW(), NOW(), (SELECT id FROM items WHERE code = 'ITEM-003'), 'Steel Box BOM', 1.0);

INSERT INTO bom_components (created_at, updated_at, bom_id, item_id, quantity)
VALUES (NOW(), NOW(), (SELECT id FROM boms WHERE name = 'Steel Box BOM'), (SELECT id FROM items WHERE code = 'ITEM-001'), 2.0); -- 2 Sheets

INSERT INTO bom_components (created_at, updated_at, bom_id, item_id, quantity)
VALUES (NOW(), NOW(), (SELECT id FROM boms WHERE name = 'Steel Box BOM'), (SELECT id FROM items WHERE code = 'ITEM-002'), 8.0); -- 8 Bolts
