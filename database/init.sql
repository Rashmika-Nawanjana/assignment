-- Sun Travels — local MySQL setup WITHOUT Docker
-- Run once as root (Workbench or: mysql -u root -p < database/init.sql)
-- Prefer Docker when possible: docker compose up -d

CREATE DATABASE IF NOT EXISTS suntravels
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

CREATE USER IF NOT EXISTS 'suntravels'@'localhost' IDENTIFIED BY 'suntravels';
CREATE USER IF NOT EXISTS 'suntravels'@'%' IDENTIFIED BY 'suntravels';
GRANT ALL PRIVILEGES ON suntravels.* TO 'suntravels'@'localhost';
GRANT ALL PRIVILEGES ON suntravels.* TO 'suntravels'@'%';
FLUSH PRIVILEGES;

-- Then create tables (from project root):
--   mysql -u suntravels -psuntravels suntravels < database/schema.sql
