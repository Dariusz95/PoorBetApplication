-- Indeks kluczowy dla OutboxPublisher — zapytanie wykonywane co 5 sekund
-- Coupon-service ma analogiczny indeks (idx_coupon_outbox_status_created_at)
CREATE INDEX IF NOT EXISTS idx_wallet_outbox_status_created_at
    ON outbox_event(status, created_at);
