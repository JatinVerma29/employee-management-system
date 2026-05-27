-- ==============================
-- V1: Initial Schema
-- ==============================

-- Roles table
CREATE TABLE roles (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(50) NOT NULL UNIQUE,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Departments table
CREATE TABLE departments (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(100) NOT NULL UNIQUE,
    code        VARCHAR(20)  NOT NULL UNIQUE,
    description TEXT,
    is_active   BOOLEAN NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Users table (for auth)
CREATE TABLE users (
    id          BIGSERIAL PRIMARY KEY,
    username    VARCHAR(50)  NOT NULL UNIQUE,
    email       VARCHAR(100) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    is_active   BOOLEAN NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

-- User-Role mapping
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role_id BIGINT NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

-- Employees table
CREATE TABLE employees (
    id              BIGSERIAL PRIMARY KEY,
    employee_code   VARCHAR(20)  NOT NULL UNIQUE,
    first_name      VARCHAR(100) NOT NULL,
    last_name       VARCHAR(100) NOT NULL,
    email           VARCHAR(150) NOT NULL UNIQUE,
    phone           VARCHAR(20),
    date_of_birth   DATE,
    gender          VARCHAR(10),
    address         TEXT,
    designation     VARCHAR(100) NOT NULL,
    salary          DECIMAL(15,2) NOT NULL,
    hire_date       DATE NOT NULL,
    employment_type VARCHAR(20)  NOT NULL DEFAULT 'FULL_TIME',
    status          VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    department_id   BIGINT REFERENCES departments(id) ON DELETE SET NULL,
    manager_id      BIGINT REFERENCES employees(id) ON DELETE SET NULL,
    user_id         BIGINT REFERENCES users(id) ON DELETE SET NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Refresh tokens table
CREATE TABLE refresh_tokens (
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token       VARCHAR(512) NOT NULL UNIQUE,
    expiry_date TIMESTAMP NOT NULL,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Indexes
CREATE INDEX idx_employees_department ON employees(department_id);
CREATE INDEX idx_employees_status     ON employees(status);
CREATE INDEX idx_employees_email      ON employees(email);
CREATE INDEX idx_users_username       ON users(username);
CREATE INDEX idx_refresh_tokens_token ON refresh_tokens(token);

-- ==============================
-- Seed Data
-- ==============================
INSERT INTO roles(name) VALUES ('ROLE_ADMIN'), ('ROLE_HR'), ('ROLE_EMPLOYEE');

INSERT INTO departments(name, code, description) VALUES
    ('Engineering',     'ENG',  'Software Engineering & Development'),
    ('Human Resources', 'HR',   'People & Culture'),
    ('Finance',         'FIN',  'Finance & Accounting'),
    ('Marketing',       'MKT',  'Marketing & Growth'),
    ('Operations',      'OPS',  'Business Operations');

-- Admin user: password = Admin@123 (BCrypt hashed)
INSERT INTO users(username, email, password) VALUES
    ('admin', 'admin@ems.com', '$2a$12$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy');

INSERT INTO user_roles(user_id, role_id)
    SELECT u.id, r.id FROM users u, roles r WHERE u.username = 'admin';
