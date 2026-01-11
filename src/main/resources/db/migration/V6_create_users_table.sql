-- Create Users Table
CREATE TABLE users (
    id UUID PRIMARY KEY,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(100) NOT NULL,
    role VARCHAR(50) NOT NULL DEFAULT 'EMPLOYEE',
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create Index on email for faster lookups
CREATE INDEX idx_users_email ON users (email);

-- Insert default test users
-- Password: password123 (you should hash these in production)
INSERT INTO
    users (
        id,
        email,
        password,
        name,
        role,
        active
    )
VALUES (
        '550e8400-e29b-41d4-a716-446655440001',
        'admin@ubs.com',
        'password123',
        'Admin User',
        'ADMIN',
        true
    ),
    (
        '550e8400-e29b-41d4-a716-446655440002',
        'manager@ubs.com',
        'password123',
        'Manager User',
        'MANAGER',
        true
    ),
    (
        '550e8400-e29b-41d4-a716-446655440003',
        'employee@ubs.com',
        'password123',
        'Employee User',
        'EMPLOYEE',
        true
    );