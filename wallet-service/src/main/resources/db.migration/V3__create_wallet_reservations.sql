CREATE TABLE wallet_reservations (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    amount NUMERIC(19, 2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP,
    CONSTRAINT chk_amount_positive CHECK (amount > 0),
    CONSTRAINT chk_wallet_reservation_status CHECK (status IN ('PENDING', 'CONFIRMED', 'CANCELLED'))
);

CREATE INDEX idx_wallet_reservations_user_id ON wallet_reservations(user_id);
CREATE INDEX idx_wallet_reservations_status_created_at ON wallet_reservations(status, created_at);
