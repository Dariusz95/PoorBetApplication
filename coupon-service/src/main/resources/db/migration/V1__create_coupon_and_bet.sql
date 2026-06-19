CREATE TABLE IF NOT EXISTS coupon (
    id UUID PRIMARY KEY,
    stake NUMERIC(19, 2) NOT NULL CHECK (stake >= 1.00),
    user_id UUID NOT NULL,
    reservation_id UUID NOT NULL,
    status VARCHAR(20) NOT NULL,
    potential_payout NUMERIC(19, 2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_coupon_user_id ON coupon(user_id);
CREATE INDEX IF NOT EXISTS idx_coupon_user_status_created_at
ON coupon (user_id, status, created_at DESC);

CREATE TABLE IF NOT EXISTS bet (
    id UUID PRIMARY KEY,
    match_id UUID NOT NULL,
    home_team_name VARCHAR(255) NOT NULL,
    away_team_name VARCHAR(255) NOT NULL,
    match_start_time TIMESTAMP WITH TIME ZONE NOT NULL,
    status VARCHAR(20) NOT NULL,
    bet_type VARCHAR(20) NOT NULL,
    odds NUMERIC(10, 2) NOT NULL,
    coupon_id UUID,
    version BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT fk_bet_coupon
        FOREIGN KEY (coupon_id)
        REFERENCES coupon(id)
        ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_bet_coupon_id ON bet(coupon_id);
CREATE INDEX IF NOT EXISTS idx_bet_match_id ON bet(match_id);