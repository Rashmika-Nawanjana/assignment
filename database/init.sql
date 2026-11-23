-- Sun Travels - local MySQL setup (no Docker needed)
-- Run once in MySQL Workbench or:
--   mysql -u root -p < database/init.sql

CREATE DATABASE IF NOT EXISTS suntravels
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

-- App user (change password if you prefer)
CREATE USER IF NOT EXISTS 'suntravels'@'localhost' IDENTIFIED BY 'suntravels';
GRANT ALL PRIVILEGES ON suntravels.* TO 'suntravels'@'localhost';
FLUSH PRIVILEGES;

USE suntravels;

-- Tables will also be created by Spring JPA (ddl-auto=update).
-- This script is enough to create the database + login for the app.
