# Employee Management System (EMS)

A **production-ready REST API backend** built with **Spring Boot 3**, **Spring Security 6 (JWT)**, and **MySQL 8**.

---

## 📋 Table of Contents

1. [Overview](#overview)
2. [Tech Stack](#tech-stack)
3. [Prerequisites](#prerequisites)
4. [Setup Instructions](#setup-instructions)
5. [Default Credentials](#default-credentials)
6. [API Endpoints](#api-endpoints)
7. [Authentication](#authentication)
8. [Role-Based Access Control](#role-based-access-control)
9. [Database Schema](#database-schema)
10. [Project Structure](#project-structure)
11. [Running Tests](#running-tests)
12. [Logging](#logging)
13. [Troubleshooting](#troubleshooting)
14. [Deployment](#deployment)

---

## Overview

**Employee Management System** is a complete backend solution for managing employees, departments, and user authentication with fine-grained role-based access control.

### Key Features

- ✅ **JWT-based Authentication** - Stateless, scalable token-based auth
- ✅ **Role-Based Access Control** - ADMIN, HR, VIEWER roles with method-level security
- ✅ **Employee CRUD** - Full create, read, update, delete operations with soft deletes
- ✅ **Department Management** - Organize employees by departments
- ✅ **Analytics Dashboard** - Real-time employee statistics and salary insights
- ✅ **Audit Logging** - Track all changes with timestamps and user info
- ✅ **Comprehensive Error Handling** - Standardized error responses
- ✅ **AOP Logging** - Cross-cutting logging with execution timing
- ✅ **Unit Tests** - 23+ tests with JUnit 5 and Mockito
- ✅ **Production Ready** - Follows Spring Boot best practices

---

## Tech Stack

| Layer | Technology | Version |
|-------|-----------|---------|
| **Framework** | Spring Boot | 3.2.0 |
| **Security** | Spring Security | 6.2.0 |
| **ORM** | Spring Data JPA / Hibernate | Latest |
| **Database** | MySQL | 8.0+ |
| **Authentication** | JWT (JJWT) | 0.12.3 |
| **Build Tool** | Maven | 3.8+ |
| **Language** | Java | 17+ |
| **Logging** | SLF4J + Logback | Latest |
| **Testing** | JUnit 5 + Mockito | Latest |
| **Utilities** | Lombok | Latest |

---

## Prerequisites

### Required Software

- **Java Development Kit (JDK)** 17 or higher
  ```bash
  java -version
  # Output: openjdk version "17.0.x" or higher
  ```

- **MySQL Server** 8.0 or higher
  ```bash
  mysql --version
  # Output: mysql  Ver 8.0.x for Linux
  ```

- **Maven** 3.8 or higher
  ```bash
  mvn --version
  # Output: Apache Maven 3.8.x
  ```

- **Git** (for cloning repository)
  ```bash
  git --version
  ```

### IDE (Recommended)

- **Eclipse IDE** for Java Developers (latest)
- **IntelliJ IDEA** Community Edition or Ultimate
- **VS Code** with Spring Boot extension

### Tools for API Testing

- **Postman** - API testing and collection management
- **cURL** - Command-line HTTP client
- **Insomnia** - REST client

---

## Setup Instructions

### Step 1: Clone or Extract Project

```bash
# If using Git
git clone https://github.com/vikram/employee-management-system.git
cd employee-management-system

# OR extract the ZIP file
unzip ems-backend.zip
cd ems
```

### Step 2: Create MySQL Database

Open **MySQL Workbench** or MySQL CLI:

```bash
mysql -u root -p
```

Then execute:

```sql
-- Run schema.sql
source src/main/resources/sql/schema.sql;

-- Run seed-data.sql (creates default users and data)
source src/main/resources/sql/seed-data.sql;

-- Verify
USE ems_db;
SHOW TABLES;
SELECT * FROM users;
SELECT * FROM employees;
```

**Or in MySQL Workbench:**
1. File → Open SQL Script → Select `schema.sql`
2. Execute (Ctrl+Shift+Enter)
3. File → Open SQL Script → Select `seed-data.sql`
4. Execute

### Step 3: Configure Database Connection

**File:** `src/main/resources/application.yml`

Find this section and update your MySQL password:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/ems_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
    username: root
    password: your_mysql_password  # ← CHANGE THIS
    driver-class-name: com.mysql.cj.jdbc.Driver
```

### Step 4: Import Project into Eclipse

1. **Eclipse → File → Import → Maven → Existing Maven Projects**
2. Select the project root folder
3. Click **Finish**
4. Wait for Maven to download dependencies (watch the bottom status bar)
5. Right-click project → **Maven → Update Project** (if needed)

### Step 5: Run the Application

**Option A: From Eclipse**

1. Right-click `EmployeeManagementSystemApplication.java`
2. **Run As → Spring Boot App**
3. Check console for: `"Started EmployeeManagementSystemApplication in X.XXX seconds"`

**Option B: From Command Line**

```bash
mvn clean spring-boot:run
```

**Option C: Build JAR and Run**

```bash
mvn clean package
java -jar target/employee-management-system-1.0.0.jar
```

### Step 6: Verify Application is Running

```bash
curl http://localhost:8080/api/auth/login
# Should return: 400 Bad Request (no credentials provided)
```

Or open Postman and test the login endpoint.

---

## Default Credentials

Three default users are created by `seed-data.sql`:

| Username | Password  | Role         | Email          |
|----------|-----------|--------------|----------------|
| `admin`  | `password`| ROLE_ADMIN   | admin@ems.com  |
| `hruser` | `password`| ROLE_HR      | hr@ems.com     |
| `viewer` | `password`| ROLE_VIEWER  | viewer@ems.com |

### First Login Test

**Using Postman:**

1. Create new request: **POST** `http://localhost:8080/api/auth/login`
2. Headers:
   ```
   Content-Type: application/json
   ```
3. Body (raw JSON):
   ```json
   {
     "username": "admin",
     "password": "password"
   }
   ```
4. Send request
5. Response (200 OK):
   ```json
   {
     "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
     "type": "Bearer",
     "id": 1,
     "username": "admin",
     "email": "admin@ems.com",
     "roles": ["ROLE_ADMIN"]
   }
   ```

---

## API Endpoints

### Authentication Endpoints

#### POST /api/auth/login
**Public** - No authentication required

Login and get JWT token.

**Request:**
```json
{
  "username": "admin",
  "password": "password"
}
```

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "id": 1,
  "username": "admin",
  "email": "admin@ems.com",
  "roles": ["ROLE_ADMIN"]
}
```

**Errors:**
- `400 Bad Request` - Missing username or password
- `401 Unauthorized` - Wrong password
- `404 Not Found` - User doesn't exist

---

#### POST /api/auth/register
**Role Required:** ADMIN

Register a new user.

**Request:**
```json
{
  "username": "newuser",
  "password": "password123",
  "email": "new@ems.com",
  "roles": ["ROLE_HR"]
}
```

**Response (200 OK):**
```json
"User registered successfully"
```

**Errors:**
- `400 Bad Request` - Invalid email or password < 6 chars
- `403 Forbidden` - User is not ADMIN
- `409 Conflict` - Username or email already exists

---

### Employee Endpoints

#### GET /api/employees
**Role Required:** ADMIN, HR, VIEWER

Get paginated list of employees with optional filters.

**Query Parameters:**
```
page=0          (default: 0)
size=10         (default: 10)
sortBy=id       (default: id - sort field)
status=ACTIVE   (optional: ACTIVE | INACTIVE | ON_LEAVE)
departmentId=1  (optional: department ID)
```

**Example Request:**
```
GET /api/employees?page=0&size=10&status=ACTIVE&departmentId=1
Authorization: Bearer <token>
```

**Response (200 OK):**
```json
{
  "content": [
    {
      "id": 1,
      "firstName": "Vikram",
      "lastName": "Kumar",
      "email": "vikram@ems.com",
      "phone": "9876543210",
      "salary": 75000,
      "hireDate": "2023-01-15",
      "status": "ACTIVE",
      "departmentId": 1,
      "departmentName": "Engineering",
      "createdAt": "2024-01-15T10:30:00",
      "updatedAt": "2024-01-15T10:30:00"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10,
    "sort": {"sorted": true, "unsorted": false}
  },
  "totalElements": 6,
  "totalPages": 1
}
```

---

#### GET /api/employees/{id}
**Role Required:** ADMIN, HR, VIEWER

Get employee by ID.

**Request:**
```
GET /api/employees/1
Authorization: Bearer <token>
```

**Response (200 OK):**
```json
{
  "id": 1,
  "firstName": "Vikram",
  "lastName": "Kumar",
  "email": "vikram@ems.com",
  "phone": "9876543210",
  "salary": 75000,
  "hireDate": "2023-01-15",
  "status": "ACTIVE",
  "departmentId": 1,
  "departmentName": "Engineering",
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-15T10:30:00"
}
```

**Errors:**
- `401 Unauthorized` - Missing/invalid token
- `404 Not Found` - Employee doesn't exist

---

#### GET /api/employees/search
**Role Required:** ADMIN, HR

Search employees by keyword (name, email).

**Query Parameters:**
```
keyword=vikram  (search in firstName, lastName, email)
page=0
size=10
```

**Request:**
```
GET /api/employees/search?keyword=vikram&page=0&size=10
Authorization: Bearer <token>
```

---

#### POST /api/employees
**Role Required:** ADMIN, HR

Create new employee.

**Request:**
```json
{
  "firstName": "Ravi",
  "lastName": "Kumar",
  "email": "ravi@ems.com",
  "phone": "9876543299",
  "salary": 60000,
  "hireDate": "2024-01-10",
  "departmentId": 1
}
```

**Validation Rules:**
- `firstName`: Required, 2-50 characters
- `lastName`: Required, 2-50 characters
- `email`: Required, valid email format, UNIQUE
- `phone`: Optional, 10-15 digits
- `salary`: Optional, must be positive
- `hireDate`: Optional, cannot be in future
- `departmentId`: Required, must exist

**Response (201 Created):**
```
Location: /api/employees/7
```
```json
{
  "id": 7,
  "firstName": "Ravi",
  "lastName": "Kumar",
  "email": "ravi@ems.com",
  "phone": "9876543299",
  "salary": 60000,
  "hireDate": "2024-01-10",
  "status": "ACTIVE",
  "departmentId": 1,
  "departmentName": "Engineering",
  "createdAt": "2024-01-15T11:00:00",
  "updatedAt": "2024-01-15T11:00:00"
}
```

**Errors:**
- `400 Bad Request` - Validation failed
- `403 Forbidden` - User is VIEWER (cannot create)
- `404 Not Found` - Department doesn't exist
- `409 Conflict` - Email already exists

---

#### PUT /api/employees/{id}
**Role Required:** ADMIN, HR

Update employee (all fields).

**Request:**
```json
{
  "firstName": "Ravi",
  "lastName": "Kumar",
  "email": "ravi@ems.com",
  "phone": "9876543299",
  "salary": 65000,
  "hireDate": "2024-01-10",
  "departmentId": 1
}
```

**Response (200 OK):**
```json
{ /* Updated employee */ }
```

---

#### PATCH /api/employees/{id}/status
**Role Required:** ADMIN

Change employee status only.

**Query Parameter:**
```
status=ON_LEAVE  (ACTIVE | INACTIVE | ON_LEAVE)
```

**Request:**
```
PATCH /api/employees/1/status?status=ON_LEAVE
Authorization: Bearer <admin_token>
```

**Response (200 OK):**
```json
{
  "id": 1,
  "firstName": "Vikram",
  "lastName": "Kumar",
  "status": "ON_LEAVE",
  /* ... other fields ... */
}
```

---

#### DELETE /api/employees/{id}
**Role Required:** ADMIN

Soft delete employee (sets status to INACTIVE).

**Request:**
```
DELETE /api/employees/1
Authorization: Bearer <admin_token>
```

**Response (204 No Content)**
```
(empty body)
```

---

### Department Endpoints

#### GET /api/departments
**Role Required:** Any authenticated user

Get all departments.

**Request:**
```
GET /api/departments
Authorization: Bearer <token>
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "name": "Engineering",
    "description": "Software development and architecture",
    "employeeCount": 3,
    "createdAt": "2024-01-15T10:30:00"
  },
  {
    "id": 2,
    "name": "HR",
    "description": "Human Resources operations",
    "employeeCount": 1,
    "createdAt": "2024-01-15T10:30:00"
  }
]
```

---

#### POST /api/departments
**Role Required:** ADMIN

Create department.

**Request:**
```json
{
  "name": "DevOps",
  "description": "Infrastructure and deployment"
}
```

**Response (201 Created):**
```json
{
  "id": 6,
  "name": "DevOps",
  "description": "Infrastructure and deployment",
  "employeeCount": 0,
  "createdAt": "2024-01-15T12:00:00"
}
```

---

#### PUT /api/departments/{id}
**Role Required:** ADMIN

Update department.

**Request:**
```json
{
  "name": "DevOps",
  "description": "Updated description"
}
```

---

#### DELETE /api/departments/{id}
**Role Required:** ADMIN

Delete department.

**Response (204 No Content)**

---

### Analytics Endpoints

#### GET /api/analytics/summary
**Role Required:** ADMIN, HR

Get employee count summary.

**Request:**
```
GET /api/analytics/summary
Authorization: Bearer <token>
```

**Response (200 OK):**
```json
{
  "totalEmployees": 6,
  "activeEmployees": 5,
  "inactiveEmployees": 1,
  "onLeaveEmployees": 0,
  "totalDepartments": 5
}
```

---

#### GET /api/analytics/by-department
**Role Required:** ADMIN, HR

Get employee count per department.

**Response (200 OK):**
```json
[
  {
    "department": "Engineering",
    "count": 3
  },
  {
    "department": "HR",
    "count": 1
  }
]
```

---

#### GET /api/analytics/salary-stats
**Role Required:** ADMIN (sensitive data!)

Get salary statistics per department.

**Response (200 OK):**
```json
[
  {
    "department": "Engineering",
    "averageSalary": 76666,
    "minSalary": 70000,
    "maxSalary": 80000
  }
]
```

---

#### GET /api/analytics/recent-hires
**Role Required:** ADMIN, HR

Get employees hired in last N days.

**Query Parameter:**
```
days=30  (default: 30)
```

**Response (200 OK):**
```json
[
  {
    "id": 6,
    "firstName": "Sneha",
    "lastName": "Iyer",
    "email": "sneha@ems.com",
    "hireDate": "2024-02-14",
    "status": "ACTIVE",
    "departmentName": "HR"
  }
]
```

---

## Authentication

### How JWT Works

1. **Login** - Send username/password to `/api/auth/login`
2. **Receive Token** - Get JWT token in response
3. **Store Token** - Save in browser/app memory or localStorage
4. **Include in Requests** - Add `Authorization: Bearer <token>` header
5. **Token Expires** - After 24 hours, token becomes invalid (need to login again)

### Token Format

JWT tokens have 3 parts separated by dots:

```
Header.Payload.Signature

eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.
eyJzdWIiOiJhZG1pbiIsImlhdCI6MTcwNDgwMzUwMCwiZXhwIjoxNzA0ODg5OTAwfQ.
signature
```

**Decoded Payload:**
```json
{
  "sub": "admin",           // subject (username)
  "iat": 1704803500,        // issued at (unix timestamp)
  "exp": 1704889900         // expiration (unix timestamp)
}
```

### Using Token in Postman

1. Login and copy the token value
2. In Postman, go to **Headers** tab
3. Add header:
   ```
   Key: Authorization
   Value: Bearer eyJhbGciOiJ...
   ```
4. Send request

### Token Expiration

- **Default:** 24 hours
- **After Expiration:** 401 Unauthorized error
- **Solution:** Login again to get new token

---

## Role-Based Access Control

### Role Matrix

| Endpoint | ADMIN | HR | VIEWER |
|----------|-------|----|----|
| POST /api/auth/login | ✓ | ✓ | ✓ |
| POST /api/auth/register | ✓ | ✗ | ✗ |
| GET /api/employees | ✓ | ✓ | ✓ |
| GET /api/employees/{id} | ✓ | ✓ | ✓ |
| GET /api/employees/search | ✓ | ✓ | ✗ |
| POST /api/employees | ✓ | ✓ | ✗ |
| PUT /api/employees/{id} | ✓ | ✓ | ✗ |
| PATCH /api/employees/{id}/status | ✓ | ✗ | ✗ |
| DELETE /api/employees/{id} | ✓ | ✗ | ✗ |
| GET /api/departments | ✓ | ✓ | ✓ |
| POST /api/departments | ✓ | ✗ | ✗ |
| GET /api/analytics/summary | ✓ | ✓ | ✗ |
| GET /api/analytics/salary-stats | ✓ | ✗ | ✗ |

### Error Responses

**403 Forbidden** - User lacks required role:
```json
{
  "status": 403,
  "error": "Forbidden",
  "message": "You do not have permission to perform this action",
  "path": "/api/employees",
  "timestamp": "2024-01-15T10:30:00"
}
```

---

## Database Schema

### Entity Relationships

```
users ←→ roles (M:N via user_roles)
users → employees (1:N, created_by)
departments ← employees (1:N)
users → audit_logs (1:N)
```

### Tables

- **users** - System users for authentication
- **roles** - ROLE_ADMIN, ROLE_HR, ROLE_VIEWER
- **user_roles** - M:N relationship table
- **departments** - Organization departments
- **employees** - Employee records (main entity)
- **audit_logs** - Tracking of all changes

### Database Diagram

```
┌──────────────────┐
│     roles        │
├──────────────────┤
│ id (PK)          │
│ name (UNIQUE)    │
└──────────────────┘
       ↑      ↑
       │      └────────────────────┐
       │                           │
┌──────────────────┐       ┌──────────────────┐
│   user_roles     │       │      users       │
├──────────────────┤       ├──────────────────┤
│ user_id (FK, PK) │       │ id (PK)          │
│ role_id (FK, PK) │────→  │ username (UNIQUE)│
└──────────────────┘       │ password (BCrypt)│
                           │ email (UNIQUE)   │
                           │ enabled          │
                           │ created_at       │
                           └──────────────────┘
                                   ↓
                           ┌──────────────────┐
                           │  employees       │
                           ├──────────────────┤
                           │ id (PK)          │
                           │ first_name       │
                           │ last_name        │
                           │ email (UNIQUE)   │
                           │ phone            │
                           │ salary           │
                           │ hire_date        │
                           │ status (ENUM)    │
                           │ department_id (FK)
                           │ created_by (FK)  │
                           │ created_at       │
                           │ updated_at       │
                           └──────────────────┘
                                   ↑
                                   │
                           ┌──────────────────┐
                           │  departments     │
                           ├──────────────────┤
                           │ id (PK)          │
                           │ name (UNIQUE)    │
                           │ description      │
                           │ created_at       │
                           └──────────────────┘

┌──────────────────┐
│   audit_logs     │
├──────────────────┤
│ id (PK)          │
│ action           │
│ entity_type      │
│ entity_id        │
│ performed_by (FK)→ users
│ old_value        │
│ new_value        │
│ timestamp        │
└──────────────────┘
```

---

## Project Structure

```
employee-management-system/
│
├── src/main/java/com/vikram/ems/
│   ├── EmployeeManagementSystemApplication.java
│   ├── config/
│   │   └── SecurityConfig.java
│   ├── controller/
│   │   ├── AuthController.java
│   │   ├── EmployeeController.java
│   │   ├── DepartmentController.java
│   │   └── AnalyticsController.java
│   ├── service/
│   │   ├── AuthService.java
│   │   ├── EmployeeService.java
│   │   ├── DepartmentService.java
│   │   ├── AnalyticsService.java
│   │   └── impl/
│   │       ├── AuthServiceImpl.java
│   │       ├── EmployeeServiceImpl.java
│   │       ├── DepartmentServiceImpl.java
│   │       └── AnalyticsServiceImpl.java
│   ├── repository/
│   │   ├── UserRepository.java
│   │   ├── RoleRepository.java
│   │   ├── EmployeeRepository.java
│   │   ├── DepartmentRepository.java
│   │   └── AuditLogRepository.java
│   ├── entity/
│   │   ├── User.java
│   │   ├── Role.java
│   │   ├── Employee.java
│   │   ├── Department.java
│   │   ├── AuditLog.java
│   │   └── EmployeeStatus.java
│   ├── dto/
│   │   ├── request/
│   │   │   ├── LoginRequest.java
│   │   │   ├── RegisterRequest.java
│   │   │   ├── EmployeeRequest.java
│   │   │   └── DepartmentRequest.java
│   │   └── response/
│   │       ├── JwtResponse.java
│   │       ├── EmployeeResponse.java
│   │       ├── DepartmentResponse.java
│   │       ├── ErrorResponse.java
│   │       └── AnalyticsResponses.java
│   ├── security/
│   │   ├── JwtTokenProvider.java
│   │   ├── JwtAuthenticationFilter.java
│   │   ├── JwtAuthEntryPoint.java
│   │   ├── UserDetailsImpl.java
│   │   └── UserDetailsServiceImpl.java
│   ├── exception/
│   │   ├── GlobalExceptionHandler.java
│   │   ├── ResourceNotFoundException.java
│   │   └── DuplicateResourceException.java
│   ├── logging/
│   │   └── LoggingAspect.java
│   └── util/
│       └── SecurityUtil.java
│
├── src/main/resources/
│   ├── application.yml
│   ├── logback-spring.xml
│   └── sql/
│       ├── schema.sql
│       └── seed-data.sql
│
├── src/test/java/com/vikram/ems/
│   ├── service/
│   │   ├── EmployeeServiceTest.java
│   │   └── AuthServiceTest.java
│   ├── security/
│   │   └── JwtTokenProviderTest.java
│   └── controller/
│       └── AuthControllerTest.java
│
├── target/                    (Maven builds here)
│   ├── classes/
│   │   └── com/vikram/ems/** (compiled .class files)
│   ├── test-classes/
│   │   └── com/vikram/ems/** (compiled test classes)
│   ├── surefire-reports/
│   │   └── (JUnit test results)
│   └── employee-management-system-1.0.0.jar (executable JAR)
│
├── pom.xml                    (Maven configuration)
├── README.md                  (this file)
├── .gitignore                 (Git ignore rules)
└── .git/                      (Git repository)
```

---

## Running Tests

### Run All Tests

```bash
mvn test
```

### Run Specific Test Class

```bash
mvn test -Dtest=EmployeeServiceTest
mvn test -Dtest=AuthServiceTest
mvn test -Dtest=JwtTokenProviderTest
mvn test -Dtest=AuthControllerTest
```

### Run Specific Test Method

```bash
mvn test -Dtest=EmployeeServiceTest#findById_success
mvn test -Dtest=AuthServiceTest#login_success
```

### View Test Results

```bash
# Run tests with coverage
mvn test jacoco:report

# View coverage report
# Open: target/site/jacoco/index.html in browser
```

### Test Statistics

| Class | Test Methods | Status |
|-------|-------------|--------|
| EmployeeServiceTest | 9 | ✓ Passing |
| AuthServiceTest | 5 | ✓ Passing |
| JwtTokenProviderTest | 5 | ✓ Passing |
| AuthControllerTest | 4 | ✓ Passing |
| **Total** | **23** | **✓ Passing** |

---

## Logging

### Log Levels

- **DEBUG** - Detailed information (development)
- **INFO** - General information (production)
- **WARN** - Warning messages
- **ERROR** - Error messages
- **FATAL** - Fatal errors

### Configuration

**File:** `src/main/resources/application.yml`

```yaml
logging:
  level:
    com.vikram.ems: DEBUG           # Your app (verbose)
    org.springframework.security: INFO # Security (less verbose)
  file:
    name: logs/ems-application.log  # Log file location
```

### Log Output Examples

**Console:**
```
2024-01-15 10:30:00.123 [main] DEBUG com.vikram.ems.security.JwtTokenProvider - → JwtTokenProvider.generateToken() called with args: [...]
2024-01-15 10:30:00.456 [http-nio-8080-exec-1] INFO com.vikram.ems.controller.AuthController - [API] AuthController.login()
2024-01-15 10:30:00.789 [http-nio-8080-exec-1] DEBUG com.vikram.ems.service.impl.AuthServiceImpl - ← AuthServiceImpl.login() completed in 45ms
```

**File:** `logs/ems-application.log`
```
2024-01-15 10:30:00.123 [main] DEBUG com.vikram.ems.EmployeeManagementSystemApplication - Starting application
2024-01-15 10:30:02.456 [main] INFO  com.vikram.ems.EmployeeManagementSystemApplication - Started EmployeeManagementSystemApplication in 2.456 seconds
```

### View Logs

**From Command Line:**
```bash
# Follow logs in real-time
tail -f logs/ems-application.log

# Last 50 lines
tail -50 logs/ems-application.log

# Search for errors
grep ERROR logs/ems-application.log
```

**From Eclipse:**
1. Click **Console** tab at bottom
2. Logs appear in real-time while application running

---

## Troubleshooting

### Issue: "Cannot find database ems_db"

**Solution:**
```sql
CREATE DATABASE ems_db;
USE ems_db;
source src/main/resources/sql/schema.sql;
source src/main/resources/sql/seed-data.sql;
```

### Issue: "Access denied for user 'root'@'localhost'"

**Solution:** Update password in `application.yml`:
```yaml
spring:
  datasource:
    password: your_correct_password  # Use your MySQL password
```

### Issue: "Port 8080 already in use"

**Solution:** Change port in `application.yml`:
```yaml
server:
  port: 9090  # Use different port
```

Or kill the process using port 8080:
```bash
# On Linux/Mac
lsof -ti:8080 | xargs kill -9

# On Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F
```

### Issue: "Maven dependencies not downloading"

**Solution:**
1. Right-click project → **Maven → Update Project**
2. Force update: `mvn clean install -U`
3. Check internet connection
4. Check Maven settings.xml

### Issue: "Tests failing"

**Solution:**
1. Ensure MySQL is running
2. Database `ems_db` is created
3. Run: `mvn clean test`
4. Check test logs for errors

### Issue: "401 Unauthorized on API calls"

**Solution:**
1. Ensure token is included in headers:
   ```
   Authorization: Bearer <token>
   ```
2. Token might be expired (get new one)
3. Check role permissions for endpoint

---

## Deployment

### Build JAR for Production

```bash
mvn clean package -DskipTests
```

**Output:** `target/employee-management-system-1.0.0.jar`

### Run JAR

```bash
java -jar target/employee-management-system-1.0.0.jar
```

### Custom Configuration for Production

```bash
java -jar target/employee-management-system-1.0.0.jar \
  --server.port=8080 \
  --spring.datasource.url=jdbc:mysql://prod-db-host:3306/ems_db \
  --spring.datasource.username=produser \
  --spring.datasource.password=prodpassword \
  --app.jwt.secret=your-production-secret-key
```

### Docker Deployment (Optional)

**Dockerfile:**
```dockerfile
FROM openjdk:17-slim
COPY target/employee-management-system-1.0.0.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**Build and Run:**
```bash
docker build -t ems:1.0.0 .
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_PASSWORD=password \
  ems:1.0.0
```

---

## Key Design Decisions

1. **JWT over Sessions** - Stateless, scalable for microservices
2. **Spring Data JPA** - Type-safe queries, less boilerplate
3. **Soft Deletes** - Preserve data for audit trails
4. **BCrypt Passwords** - Industry standard, slow hashing
5. **Role-Based Security** - Fine-grained access control
6. **AOP Logging** - Cross-cutting concerns cleanly separated
7. **Global Exception Handler** - Consistent error responses
8. **DTOs** - Decouple API from entities

---

## Contributing

When adding new features:

1. Follow existing code patterns
2. Add unit tests for new code
3. Update this README
4. Ensure all tests pass: `mvn test`
5. Build JAR successfully: `mvn clean package`

---

## License

This project is open source and available under the MIT License.

---

## Support

For issues or questions:

1. Check the **Troubleshooting** section
2. Review the **COMPLETE_DETAILED_BLUEPRINT.md**
3. Check **ECLIPSE_PACKAGE_AND_CLASS_BLUEPRINT.md**
4. Search existing issues
5. Create a new issue with detailed description

---

## Quick Reference

### Start Application
```bash
mvn spring-boot:run
```

### Run Tests
```bash
mvn test
```

### Build JAR
```bash
mvn clean package
```

### Run JAR
```bash
java -jar target/employee-management-system-1.0.0.jar
```

### View Logs
```bash
tail -f logs/ems-application.log
```

---

**Last Updated:** January 2024  
**Version:** 1.0.0  
**Author:** Vikram  

Happy coding! 🚀
