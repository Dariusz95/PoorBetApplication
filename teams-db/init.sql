CREATE USER teams_user WITH PASSWORD 'teams_pwd';
CREATE DATABASE teamsdb OWNER teams_user;
GRANT ALL PRIVILEGES ON DATABASE teamsdb TO teams_user;