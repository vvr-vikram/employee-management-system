-- ============================================
-- Seed Data
-- ============================================
USE ems_db;

-- Roles
INSERT INTO roles (name) VALUES
('ROLE_ADMIN'),
('ROLE_HR'),
('ROLE_VIEWER');

-- Admin user (password: admin123 - BCrypt encoded)
INSERT INTO users (username, password, email, enabled) VALUES
('admin', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'admin@ems.com', true),
('hruser', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'hr@ems.com', true),
('viewer', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'viewer@ems.com', true);

-- Assign roles
INSERT INTO user_roles (user_id, role_id) VALUES
(1, 1),  -- admin -> ROLE_ADMIN
(2, 2),  -- hruser -> ROLE_HR
(3, 3);  -- viewer -> ROLE_VIEWER

-- Departments
INSERT INTO departments (name, description) VALUES
('Engineering', 'Software development and architecture'),
('Human Resources', 'HR operations and recruitment'),
('Finance', 'Accounts and financial planning'),
('Marketing', 'Brand and marketing operations'),
('Operations', 'Business operations and logistics');

-- Sample Employees
INSERT INTO employees (first_name, last_name, email, phone, salary, hire_date, status, department_id, created_by) VALUES
('Vikram',  'V',   'vikram@ems.com',  '9876543210', 75000.00, '2023-01-15', 'ACTIVE',   1, 1),
('Priya',   'S',  'priya@ems.com',   '9876543211', 65000.00, '2023-03-20', 'ACTIVE',   2, 1),
('Arjun',   'S',   'arjun@ems.com',   '9876543212', 80000.00, '2022-11-10', 'ACTIVE',   1, 1),
('Divya',   'P',   'divya@ems.com',   '9876543213', 55000.00, '2024-01-05', 'ACTIVE',   3, 1),
('Rahul',   'N',    'rahul@ems.com',   '9876543214', 70000.00, '2023-06-01', 'INACTIVE', 4, 1),
('Sneha',   'D',    'sneha@ems.com',   '9876543215', 60000.00, '2024-02-14', 'ACTIVE',   2, 1);