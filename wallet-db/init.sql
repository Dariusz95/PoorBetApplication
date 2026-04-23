CREATE USER wallet_user WITH PASSWORD 'wallet_pwd';
CREATE DATABASE walletdb OWNER wallet_user;
GRANT ALL PRIVILEGES ON DATABASE walletdb TO wallet_user;
