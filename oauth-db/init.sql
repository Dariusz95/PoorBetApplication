CREATE USER oauth_user WITH PASSWORD 'oauth_pwd';
CREATE DATABASE oauthdb OWNER oauth_user;
GRANT ALL PRIVILEGES ON DATABASE oauthdb TO oauth_user;