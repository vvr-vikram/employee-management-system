-- ============================================
-- Employee Management System - Database Schema
-- ============================================

CREATE DATABASE IF NOT EXISTS ems_db;
USE ems_db;

-- Roles table
CREATE TABLE roles (
    id   INT          PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(20)  NOT NULL UNIQUE
);

-- Users table
CREATE TABLE users (
    id         BIGINT       PRIMARY KEY AUTO_INCREMENT,
    username   VARCHAR(50)  NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    email      VARCHAR(100) NOT NULL UNIQUE,
    enabled    BOOLEAN      DEFAULT TRUE,
    created_at TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

-- User-Roles join table
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id INT    NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id)  ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id)
);

-- Departments table
CREATE TABLE departments (
    id          BIGINT       PRIMARY KEY AUTO_INCREMENT,
    name        VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    created_at  TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

-- Employees table
CREATE TABLE employees (
    id            BIGINT         PRIMARY KEY AUTO_INCREMENT,
    first_name    VARCHAR(50)    NOT NULL,
    last_name     VARCHAR(50)    NOT NULL,
    email         VARCHAR(100)   NOT NULL UNIQUE,
    phone         VARCHAR(20),
    salary        DECIMAL(10,2),
    hire_date     DATE,
    status        VARCHAR(20)    DEFAULT 'ACTIVE',
    department_id BIGINT,
    created_by    BIGINT,
    created_at    TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (department_id) REFERENCES departments(id) ON DELETE SET NULL,
    FOREIGN KEY (created_by)    REFERENCES users(id)
);

-- Audit Logs table
CREATE TABLE audit_logs (
    id           BIGINT       PRIMARY KEY AUTO_INCREMENT,
    action       VARCHAR(100) NOT NULL,
    entity_type  VARCHAR(50),
    entity_id    BIGINT,
    performed_by BIGINT,
    old_value    TEXT,
    new_value    TEXT,
    timestamp    TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (performed_by) REFERENCES users(id)
);