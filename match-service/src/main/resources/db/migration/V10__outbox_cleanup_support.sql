CREATE INDEX IF NOT EXISTS idx_match_outbox_status_sent_at ON outbox_event(status, sent_at);

CREATE INDEX IF NOT EXISTS idx_match_outbox_status_next_retry_at ON outbox_event(status, next_retry_at);

UPDATE outbox_event SET sent_at = created_at WHERE status = 'SENT' AND sent_at IS NULL;
