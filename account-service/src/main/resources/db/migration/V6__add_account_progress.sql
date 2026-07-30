CREATE TABLE level_config (
    level               INT PRIMARY KEY,
    required_experience BIGINT NOT NULL,
    win_bonus_percent   INT NOT NULL,
    daily_coupon_limit  INT NOT NULL
);

INSERT INTO level_config (level, required_experience, win_bonus_percent, daily_coupon_limit) VALUES
    (1, 0,     1, 3),
    (2, 100,   2, 4),
    (3, 250,   3, 5),
    (4, 500,   4, 6),
    (5, 900,   5, 8),
    (6, 1500,  6, 10),
    (7, 2300,  7, 12),
    (8, 3400,  8, 15),
    (9, 4800,  9, 18),
    (10, 6500, 10, 20),
    (11, 8500, 11, 23),
    (12, 11000,12, 26),
    (13, 14000,13, 30),
    (14, 17500,14, 35),
    (15, 21500,15, 40);

CREATE TABLE account_progress (
    user_id     UUID PRIMARY KEY,
    level       INT NOT NULL DEFAULT 1 REFERENCES level_config(level),
    current_exp BIGINT NOT NULL DEFAULT 0,
    updated_at  TIMESTAMP,
    CONSTRAINT chk_account_progress_current_exp_non_negative CHECK (current_exp >= 0)
);

CREATE TABLE processed_event (
    event_id     UUID PRIMARY KEY,
    processed_at TIMESTAMP NOT NULL DEFAULT NOW()
);
