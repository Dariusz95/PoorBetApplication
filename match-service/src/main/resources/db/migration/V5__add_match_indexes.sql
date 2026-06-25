-- Indeks dla wyszukiwania meczów po puli (najczęstsze zapytanie — countByPoolIdAndStatus, getResults)
CREATE INDEX IF NOT EXISTS idx_match_pool_id ON match(pool_id);

-- Indeks złożony dla filtrowania po puli i statusie (używany m.in. w LiveMatchSimulationManager)
CREATE INDEX IF NOT EXISTS idx_match_pool_id_status ON match(pool_id, status);

-- Indeksy dla drużyn (lookup przy budowaniu DTO)
CREATE INDEX IF NOT EXISTS idx_match_home_team_id ON match(home_team_id);
CREATE INDEX IF NOT EXISTS idx_match_away_team_id ON match(away_team_id);
