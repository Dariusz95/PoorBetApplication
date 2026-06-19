CREATE TABLE teams (
  id UUID PRIMARY KEY,
  name VARCHAR(255) NOT NULL UNIQUE,
  img VARCHAR(1024),
  attack_power INT NOT NULL,
  defence_power INT NOT NULL,
  created_at TIMESTAMP DEFAULT now()
);
