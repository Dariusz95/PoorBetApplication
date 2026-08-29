-- Retry support for the outbox publisher: attempt counter + earliest-next-attempt timestamp.
ALTER TABLE outbox_event ADD COLUMN retry_count INT NOT NULL DEFAULT 0;
ALTER TABLE outbox_event ADD COLUMN next_retry_at TIMESTAMP;

-- Widen the status constraint with the new terminal state.
ALTER TABLE outbox_event DROP CONSTRAINT chk_outbox_status;
ALTER TABLE outbox_event ADD CONSTRAINT chk_outbox_status CHECK (status IN ('NEW', 'SENT', 'FAILED', 'DEAD_LETTER'));

-- Index for the periodic "delete SENT events older than X" query (filters on status + sent_at).
CREATE INDEX IF NOT EXISTS idx_coupon_outbox_status_sent_at ON outbox_event(status, sent_at);

-- Index for retry pickup: pending rows are filtered by status + next_retry_at.
CREATE INDEX IF NOT EXISTS idx_coupon_outbox_status_next_retry_at ON outbox_event(status, next_retry_at);

-- Backfill sent_at for rows published before the column was populated, otherwise the
-- cleanup predicate (sent_at < threshold) would never match them and they would leak forever.
UPDATE outbox_event SET sent_at = created_at WHERE status = 'SENT' AND sent_at IS NULL;
