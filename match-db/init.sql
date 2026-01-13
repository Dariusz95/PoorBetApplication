CREATE USER match_user WITH PASSWORD 'match_pwd';
CREATE DATABASE matchdb OWNER match_user;
GRANT ALL PRIVILEGES ON DATABASE matchdb TO match_user;