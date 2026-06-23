-- Constraint sprawdzał 'PENDING'/'SENT'/'FAILED', ale kod zapisuje status 'NEW'.
-- Każda próba zapisu eventu OutboxEvent kończyła się naruszeniem constraintu,
-- co rollbackowało całą transakcję processFinishedMatch — kupony nie były rozliczane.
ALTER TABLE outbox_event DROP CONSTRAINT chk_coupon_outbox_status;
ALTER TABLE outbox_event ADD CONSTRAINT chk_outbox_status CHECK (status IN ('NEW', 'SENT', 'FAILED'));
