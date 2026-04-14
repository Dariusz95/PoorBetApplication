CREATE TABLE wallet_reservations (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    amount NUMERIC(19, 2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
    updated_at TIMESTAMP
);

CREATE INDEX idx_wallet_reservations_user_id
ON wallet_reservations(user_id);

ALTER TABLE wallet_reservations
ADD CONSTRAINT chk_amount_positive CHECK (amount > 0);