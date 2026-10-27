CREATE TABLE app_users (
    id UUID PRIMARY KEY,
    email VARCHAR(254) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('CUSTOMER','ADMIN'))
);
ALTER TABLE accounts ADD COLUMN owner_user_id UUID REFERENCES app_users(id);
CREATE INDEX idx_accounts_owner_user_id ON accounts(owner_user_id);
-- Demo account ownership is assigned by optional local bootstrap at startup.
-- Existing accounts are not exposed to users until ownership is assigned.
