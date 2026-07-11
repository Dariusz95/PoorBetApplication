ALTER TABLE outbox_event ADD COLUMN retry_count INT NOT NULL DEFAULT 0;
ALTER TABLE outbox_event ADD COLUMN next_retry_at TIMESTAMP;

UPDATE outbox_event SET status = 'NEW', retry_count = 0, next_retry_at = NULL WHERE status = 'FAILED';

ALTER TABLE outbox_event DROP CONSTRAINT chk_match_outbox_status;
ALTER TABLE outbox_event ADD CONSTRAINT chk_match_outbox_status CHECK (status IN ('NEW', 'SENT', 'FAILED', 'DEAD_LETTER'));
