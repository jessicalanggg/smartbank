CREATE TABLE banking_transactions (
    id UUID PRIMARY KEY,
    transaction_type VARCHAR(20) NOT NULL CHECK (transaction_type IN ('DEPOSIT','WITHDRAWAL','TRANSFER')),
    source_account_id UUID REFERENCES accounts(id),
    destination_account_id UUID REFERENCES accounts(id),
    amount NUMERIC(19,2) NOT NULL CHECK (amount > 0),
    currency VARCHAR(3) NOT NULL,
    request_key VARCHAR(100) UNIQUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT valid_transaction_accounts CHECK (
        (transaction_type = 'DEPOSIT' AND source_account_id IS NULL AND destination_account_id IS NOT NULL)
        OR (transaction_type = 'WITHDRAWAL' AND source_account_id IS NOT NULL AND destination_account_id IS NULL)
        OR (transaction_type = 'TRANSFER' AND source_account_id IS NOT NULL AND destination_account_id IS NOT NULL AND source_account_id <> destination_account_id)
    )
);
CREATE INDEX idx_tx_source_created ON banking_transactions(source_account_id, created_at DESC);
CREATE INDEX idx_tx_destination_created ON banking_transactions(destination_account_id, created_at DESC);
