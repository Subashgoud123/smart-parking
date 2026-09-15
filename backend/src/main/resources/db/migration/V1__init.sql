CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(32) NOT NULL UNIQUE
);

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(120) NOT NULL,
    phone VARCHAR(40),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE user_roles (
    user_id BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    role_id BIGINT NOT NULL REFERENCES roles (id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

CREATE TABLE vehicles (
    id BIGSERIAL PRIMARY KEY,
    plate_number VARCHAR(32) NOT NULL UNIQUE,
    vehicle_type VARCHAR(32) NOT NULL,
    owner_id BIGINT NOT NULL REFERENCES users (id),
    contact_phone VARCHAR(40),
    nickname VARCHAR(80),
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE parking_slots (
    id BIGSERIAL PRIMARY KEY,
    slot_number VARCHAR(32) NOT NULL UNIQUE,
    area VARCHAR(80) NOT NULL,
    floor VARCHAR(40) NOT NULL,
    vehicle_type VARCHAR(32) NOT NULL,
    status VARCHAR(32) NOT NULL,
    notes VARCHAR(255)
);

CREATE TABLE parking_bookings (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users (id),
    vehicle_id BIGINT NOT NULL REFERENCES vehicles (id),
    slot_id BIGINT NOT NULL REFERENCES parking_slots (id),
    start_at TIMESTAMP NOT NULL,
    end_at TIMESTAMP NOT NULL,
    status VARCHAR(32) NOT NULL,
    bulk_group_id UUID,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT bookings_time_chk CHECK (end_at > start_at)
);

CREATE TABLE parking_transactions (
    id BIGSERIAL PRIMARY KEY,
    booking_id BIGINT REFERENCES parking_bookings (id),
    slot_id BIGINT NOT NULL REFERENCES parking_slots (id),
    vehicle_id BIGINT NOT NULL REFERENCES vehicles (id),
    entry_time TIMESTAMP NOT NULL,
    exit_time TIMESTAMP,
    duration_minutes BIGINT
);

CREATE INDEX idx_bookings_slot_time ON parking_bookings (slot_id, start_at, end_at);
CREATE INDEX idx_bookings_user ON parking_bookings (user_id);
CREATE INDEX idx_bookings_status ON parking_bookings (status);
CREATE INDEX idx_slots_status_type ON parking_slots (status, vehicle_type);
CREATE INDEX idx_vehicles_owner ON vehicles (owner_id);
CREATE INDEX idx_tx_open ON parking_transactions (exit_time);

INSERT INTO roles (name) VALUES ('ADMIN'), ('CUSTOMER'), ('STAFF');
