ALTER TABLE teams
ADD COLUMN user_id UUID;

ALTER TABLE teams
ADD CONSTRAINT uk_team_user_id
UNIQUE(user_id);