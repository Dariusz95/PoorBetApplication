CREATE USER coupon_user WITH PASSWORD 'coupon_pwd';
CREATE DATABASE coupondb OWNER coupon_user;
GRANT ALL PRIVILEGES ON DATABASE coupondb TO coupon_user;