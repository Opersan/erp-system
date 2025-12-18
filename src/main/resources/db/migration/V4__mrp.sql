-- MRP Module

CREATE TABLE mrp_runs (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    run_date TIMESTAMP NOT NULL,
    horizon_days INTEGER NOT NULL
);

CREATE TABLE planned_orders (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    mrp_run_id BIGINT NOT NULL,
    item_id BIGINT NOT NULL,
    quantity DECIMAL(19, 2) NOT NULL,
    order_date TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL, -- PROPOSED, CONVERTED, IGNORED
    CONSTRAINT fk_planned_order_run FOREIGN KEY (mrp_run_id) REFERENCES mrp_runs (id),
    CONSTRAINT fk_planned_order_item FOREIGN KEY (item_id) REFERENCES items (id)
);
