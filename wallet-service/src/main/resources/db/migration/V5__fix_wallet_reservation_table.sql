CREATE TABLE IF NOT EXISTS wallet_reservation (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    amount NUMERIC(19, 2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    version BIGINT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ
);

CREATE INDEX IF NOT EXISTS idx_wallet_reservation_user_id ON wallet_reservation(user_id);
CREATE INDEX IF NOT EXISTS idx_wallet_reservation_status_created_at ON wallet_reservation(status, created_at);

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'wallet_reservation_status_check'
    ) THEN
        ALTER TABLE wallet_reservation ADD CONSTRAINT wallet_reservation_status_check
            CHECK (status IN ('RESERVED', 'COMMITTED', 'RELEASED'));
    END IF;
END $$;

DROP TABLE IF EXISTS wallet_reservations;
