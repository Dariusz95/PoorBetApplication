ALTER TABLE coupon
ADD COLUMN total_odds NUMERIC(10,2);

CREATE OR REPLACE FUNCTION numeric_product(state numeric, value numeric)
RETURNS numeric
LANGUAGE sql
IMMUTABLE
AS $$
    SELECT state * value;
$$;

CREATE AGGREGATE product(numeric) (
    SFUNC = numeric_product,
    STYPE = numeric,
    INITCOND = '1'
);

UPDATE coupon c
SET total_odds = (
    SELECT product(b.odds)
    FROM bet b
    WHERE b.coupon_id = c.id
);

ALTER TABLE coupon ALTER COLUMN total_odds SET NOT NULL;

CREATE INDEX idx_coupon_won_total_odds 
    ON coupon(total_odds DESC) WHERE status = 'WON';

CREATE INDEX idx_coupon_won_potential_payout
    ON coupon(potential_payout DESC) WHERE status = 'WON'; 