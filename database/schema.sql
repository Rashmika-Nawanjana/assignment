-- Sun Travels logical schema (matches Java entities)
USE suntravels;

CREATE TABLE IF NOT EXISTS users (
  id         BIGINT AUTO_INCREMENT PRIMARY KEY,
  username   VARCHAR(50)  NOT NULL UNIQUE,
  password   VARCHAR(255) NOT NULL,
  full_name  VARCHAR(120),
  email      VARCHAR(120),
  role       VARCHAR(20)  NOT NULL,
  active     BOOLEAN      NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS hotels (
  id    BIGINT AUTO_INCREMENT PRIMARY KEY,
  name  VARCHAR(150) NOT NULL
);

CREATE TABLE IF NOT EXISTS contracts (
  id          BIGINT AUTO_INCREMENT PRIMARY KEY,
  hotel_id    BIGINT         NOT NULL,
  valid_from  DATE           NOT NULL,
  valid_to    DATE           NOT NULL,
  markup      DECIMAL(6, 3)  NOT NULL DEFAULT 1.150,
  CONSTRAINT fk_contract_hotel
    FOREIGN KEY (hotel_id) REFERENCES hotels (id)
);

CREATE TABLE IF NOT EXISTS room_types (
  id                          BIGINT AUTO_INCREMENT PRIMARY KEY,
  contract_id                 BIGINT         NOT NULL,
  name                        VARCHAR(100)   NOT NULL,
  price_per_person_per_night  DECIMAL(12, 2) NOT NULL,
  number_of_rooms             INT            NOT NULL,
  max_adults                  INT            NOT NULL,
  CONSTRAINT fk_room_type_contract
    FOREIGN KEY (contract_id) REFERENCES contracts (id)
);

CREATE TABLE IF NOT EXISTS bookings (
  id               BIGINT AUTO_INCREMENT PRIMARY KEY,
  booked_by_id     BIGINT         NOT NULL,
  room_type_id     BIGINT         NOT NULL,
  hotel_name       VARCHAR(150)   NOT NULL,
  room_type_name   VARCHAR(100)   NOT NULL,
  check_in_date    DATE           NOT NULL,
  number_of_nights INT            NOT NULL,
  rooms_requested  INT            NOT NULL,
  total_adults     INT            NOT NULL,
  total_price      DECIMAL(14, 2) NOT NULL,
  guest_name       VARCHAR(120)   NOT NULL,
  guest_nic        VARCHAR(30),
  guest_phone      VARCHAR(30),
  guest_email      VARCHAR(120),
  status           VARCHAR(20)    NOT NULL,
  created_at       TIMESTAMP      NOT NULL,
  CONSTRAINT fk_booking_user FOREIGN KEY (booked_by_id) REFERENCES users (id),
  CONSTRAINT fk_booking_room FOREIGN KEY (room_type_id) REFERENCES room_types (id)
);

CREATE INDEX idx_contracts_dates ON contracts (valid_from, valid_to);
CREATE INDEX idx_contracts_hotel ON contracts (hotel_id);
