CREATE TABLE match_pool (
    id UUID PRIMARY KEY,
    status VARCHAR(50) NOT NULL,
    scheduled_start_time TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE match (
    id UUID PRIMARY KEY,
    home_team_id UUID NOT NULL,
    away_team_id UUID NOT NULL,
    home_goals INTEGER NOT NULL DEFAULT 0,
    away_goals INTEGER NOT NULL DEFAULT 0,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    pool_id UUID NOT NULL,

    CONSTRAINT fk_match_pool
        FOREIGN KEY (pool_id)
        REFERENCES match_pool(id)
);

CREATE TABLE match_odds (
    id UUID PRIMARY KEY,
    match_id UUID NOT NULL UNIQUE,
    home_win NUMERIC(10,2),
    draw NUMERIC(10,2),
    away_win NUMERIC(10,2),

    CONSTRAINT fk_match_odds_match
        FOREIGN KEY (match_id)
        REFERENCES match(id)
        ON DELETE CASCADE
);