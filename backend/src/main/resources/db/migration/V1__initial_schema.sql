CREATE TABLE accounts (
    id UUID PRIMARY KEY,
    account_number VARCHAR(30) NOT NULL UNIQUE,
    owner_name VARCHAR(120) NOT NULL,
    account_type VARCHAR(20) NOT NULL CHECK (account_type IN ('CHECKING', 'SAVINGS')),
    balance NUMERIC(19,2) NOT NULL CHECK (balance >= 0),
    currency VARCHAR(3) NOT NULL CHECK (currency ~ '^[A-Z]{3}$'),
    version BIGINT NOT NULL DEFAULT 0
);

-- Synthetic, public demonstration accounts only.
INSERT INTO accounts (id, account_number, owner_name, account_type, balance, currency)
VALUES
('11111111-1111-4111-8111-111111111111', 'DEMO-10001', 'Demo Customer A', 'CHECKING', 1250.00, 'CAD'),
('22222222-2222-4222-8222-222222222222', 'DEMO-10002', 'Demo Customer B', 'SAVINGS', 3800.50, 'CAD');
