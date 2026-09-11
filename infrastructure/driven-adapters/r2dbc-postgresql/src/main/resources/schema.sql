CREATE TABLE IF NOT EXISTS franchise (
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS branch (
    id           BIGSERIAL PRIMARY KEY,
    name         VARCHAR(255) NOT NULL,
    franchise_id BIGINT NOT NULL REFERENCES franchise(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS product (
    id        BIGSERIAL PRIMARY KEY,
    name      VARCHAR(255) NOT NULL,
    stock     INTEGER NOT NULL CHECK (stock >= 0),
    branch_id BIGINT NOT NULL REFERENCES branch(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_branch_franchise ON branch(franchise_id);
CREATE INDEX IF NOT EXISTS idx_product_branch   ON product(branch_id);
