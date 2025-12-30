CREATE USER match_user WITH PASSWORD 'match_pwd';
CREATE DATABASE matchdb OWNER match_user;
GRANT ALL PRIVILEGES ON DATABASE matchdb TO match_user;

\c matchdb match_user;

-- Create match_pool table
CREATE TABLE match_pool (
    id UUID PRIMARY KEY,
    status VARCHAR(50) NOT NULL,
    scheduled_start_time TIMESTAMP
);

-- Create match table
CREATE TABLE match (
    match_id UUID PRIMARY KEY,
    home_team_id UUID NOT NULL,
    away_team_id UUID NOT NULL,
    home_goals INTEGER DEFAULT 0,
    away_goals INTEGER DEFAULT 0,
    current_minute INTEGER DEFAULT 0,
    status VARCHAR(50) NOT NULL,
    match_pool_id UUID REFERENCES match_pool(id) ON DELETE CASCADE
);

-- Create join table for many-to-many relationship
CREATE TABLE match_pool_matches (
    match_pool_id UUID NOT NULL REFERENCES match_pool(id) ON DELETE CASCADE,
    matches_match_id UUID NOT NULL REFERENCES match(match_id) ON DELETE CASCADE,
    PRIMARY KEY (match_pool_id, matches_match_id)
);

-- Create indexes for better query performance
CREATE INDEX idx_match_pool_status ON match_pool(status);
CREATE INDEX idx_match_status ON match(status);
CREATE INDEX idx_match_pool_id ON match(match_pool_id);