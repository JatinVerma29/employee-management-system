# 🏢 Employee Management System

> A production-ready REST API built with **Spring Boot 3**, **PostgreSQL**, and **Docker** — demonstrating industry-standard backend development practices.

---

## 📌 Features

- **JWT Authentication** — Stateless auth with access + refresh token flow
- **Role-Based Access Control (RBAC)** — `ADMIN`, `HR`, `EMPLOYEE` roles with method-level security
- **Full CRUD** — Employees, Departments with rich search and pagination
- **Database Migrations** — Flyway-managed schema versioning
- **Global Exception Handling** — Consistent error responses across all endpoints
- **Input Validation** — Bean Validation with detailed error messages
- **API Documentation** — Swagger UI via SpringDoc OpenAPI
- **Audit Logging** — Automatic `created_at` / `updated_at` via Spring Data Auditing
- **Dockerized** — Multi-stage Docker build + Docker Compose for local dev
- **Unit Tests** — Mockito-based service layer tests with 80%+ coverage goal

---

## 🛠️ Tech Stack

| Layer        | Technology                         |
|--------------|------------------------------------|
| Language     | Java 17                            |
| Framework    | Spring Boot 3.2                    |
| Security     | Spring Security + JWT (jjwt 0.12)  |
| Database     | PostgreSQL 16                      |
| ORM          | Spring Data JPA + Hibernate        |
| Migrations   | Flyway                             |
| Build        | Maven                              |
| Testing      | JUnit 5, Mockito, AssertJ          |
| Docs         | SpringDoc OpenAPI 2 (Swagger UI)   |
| Container    | Docker + Docker Compose            |

---

## 🚀 Getting Started

### Prerequisites
- Java 17+
- Maven 3.9+
- Docker + Docker Compose

### Option 1: Docker Compose (Recommended)

```bash
# Clone the project
git clone https://github.com/yourusername/employee-management-system.git
cd employee-management-system

# Start everything (Postgres + App)
docker-compose up --build

# App runs at http://localhost:8080
```

### Option 2: Local Development

```bash
# Start PostgreSQL only
docker-compose up postgres -d

# Run the Spring Boot app
./mvnw spring-boot:run

# Or build and run the JAR
./mvnw clean package -DskipTests
java -jar target/employee-management-system-1.0.0.jar
```

---

## 🔑 API Endpoints

### Authentication
| Method | Endpoint                    | Access  | Description              |
|--------|-----------------------------|---------|--------------------------|
| POST   | `/api/v1/auth/login`        | Public  | Login, get JWT tokens    |
| POST   | `/api/v1/auth/register`     | Public  | Register new user        |
| POST   | `/api/v1/auth/refresh`      | Public  | Refresh access token     |
| POST   | `/api/v1/auth/logout`       | Auth    | Logout, revoke token     |

### Employees
| Method | Endpoint                             | Access     | Description               |
|--------|--------------------------------------|------------|---------------------------|
| POST   | `/api/v1/employees`                  | ADMIN, HR  | Create employee           |
| GET    | `/api/v1/employees`                  | All        | List all (paginated)      |
| GET    | `/api/v1/employees/{id}`             | All        | Get by ID                 |
| GET    | `/api/v1/employees/code/{code}`      | All        | Get by employee code      |
| GET    | `/api/v1/employees/search`           | All        | Search with filters       |
| GET    | `/api/v1/employees/department/{id}`  | All        | Get by department         |
| GET    | `/api/v1/employees/{id}/direct-reports` | All   | Get manager's team        |
| PUT    | `/api/v1/employees/{id}`             | ADMIN, HR  | Update employee           |
| PATCH  | `/api/v1/employees/{id}/status`      | ADMIN, HR  | Change status             |
| DELETE | `/api/v1/employees/{id}`             | ADMIN only | Delete employee           |

### Departments
| Method | Endpoint                          | Access     | Description           |
|--------|-----------------------------------|------------|-----------------------|
| POST   | `/api/v1/departments`             | ADMIN, HR  | Create department     |
| GET    | `/api/v1/departments`             | All        | List all (paginated)  |
| GET    | `/api/v1/departments/active`      | All        | List active depts     |
| GET    | `/api/v1/departments/{id}`        | All        | Get by ID             |
| PUT    | `/api/v1/departments/{id}`        | ADMIN, HR  | Update department     |
| PATCH  | `/api/v1/departments/{id}/deactivate` | ADMIN  | Deactivate            |
| DELETE | `/api/v1/departments/{id}`        | ADMIN only | Delete (if empty)     |

---

## 🔐 Quick Test

```bash
# 1. Login (default admin user)
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"Admin@123"}'

# 2. Use the returned token
TOKEN="<your_access_token>"

# 3. Create a department
curl -X POST http://localhost:8080/api/v1/departments \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name":"Backend Team","code":"BKD","description":"Backend Engineers"}'

# 4. Create an employee
curl -X POST http://localhost:8080/api/v1/employees \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Jane",
    "lastName": "Smith",
    "email": "jane.smith@company.com",
    "designation": "Senior Engineer",
    "salary": 90000,
    "hireDate": "2024-01-15",
    "departmentId": 1
  }'
```

---

## 📖 Swagger UI

After running the app, open: **http://localhost:8080/swagger-ui.html**

Authorize with your Bearer token in the top-right lock icon.

---

## 🧪 Running Tests

```bash
# Run all tests
./mvnw test

# With coverage report
./mvnw test jacoco:report
# Report: target/site/jacoco/index.html
```

---

## 📁 Project Structure

```
src/main/java/com/ems/
├── config/           # Security, OpenAPI config
├── controller/       # REST controllers (Auth, Employee, Department)
├── dto/
│   ├── request/      # Input DTOs with validation
│   └── response/     # Output DTOs (ApiResponse, PagedResponse)
├── entity/           # JPA entities (Employee, Department, User, Role)
├── exception/        # Custom exceptions + GlobalExceptionHandler
├── repository/       # Spring Data JPA repositories with custom queries
├── security/         # JWT utils, filters, UserDetailsService
├── service/
│   └── impl/         # Business logic implementations
└── util/             # EmployeeCodeGenerator, helpers

src/main/resources/
├── application.properties
└── db/migration/     # Flyway SQL scripts (V1__init_schema.sql)
```

---

## 💡 Resume Highlights

You can say this project demonstrates:

- Designed and implemented a RESTful API using Spring Boot 3 with JWT authentication, RBAC, and refresh token rotation
- Applied clean architecture with DTO pattern, service interfaces, and global exception handling
- Used Flyway for schema versioning and PostgreSQL with optimized JPA queries and custom repository methods
- Containerized the application with a multi-stage Dockerfile and Docker Compose for local development
- Achieved 80%+ service layer test coverage using JUnit 5 and Mockito
- Documented all endpoints using SpringDoc OpenAPI/Swagger UI

---

## 📄 License

MIT License — free to use for personal and commercial projects.
