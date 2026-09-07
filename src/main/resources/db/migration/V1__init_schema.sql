-- ==========================================================
-- V1__init_schema.sql
-- Initial database schema for Gym Management System
-- ==========================================================

-- 1. Members Table
CREATE TABLE members (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20),
    date_of_birth DATE
);

-- 2. Trainers Table
CREATE TABLE trainers (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20),
    specialty VARCHAR(100) NOT NULL
);

-- 3. Subscriptions Table (One-to-One with Members)
CREATE TABLE subscriptions (
    id BIGSERIAL PRIMARY KEY,
    member_id BIGINT NOT NULL UNIQUE,
    plan_name VARCHAR(50) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_subscription_member FOREIGN KEY (member_id) REFERENCES members(id) ON DELETE CASCADE
);

-- 4. Gym Classes Table (Many-to-One with Trainers)
CREATE TABLE gym_classes (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    trainer_id BIGINT NOT NULL,
    capacity INTEGER NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    CONSTRAINT fk_class_trainer FOREIGN KEY (trainer_id) REFERENCES trainers(id) ON DELETE RESTRICT
);

-- 5. Bookings Table (Many-to-One with Members and Gym Classes)
CREATE TABLE bookings (
    id BIGSERIAL PRIMARY KEY,
    member_id BIGINT NOT NULL,
    gym_class_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'CONFIRMED',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_booking_member FOREIGN KEY (member_id) REFERENCES members(id) ON DELETE CASCADE,
    CONSTRAINT fk_booking_class FOREIGN KEY (gym_class_id) REFERENCES gym_classes(id) ON DELETE CASCADE
);

-- Indexes for frequent queries (performance optimization)
CREATE INDEX idx_bookings_gym_class_id_status ON bookings(gym_class_id, status);
CREATE INDEX idx_bookings_member_id ON bookings(member_id);
