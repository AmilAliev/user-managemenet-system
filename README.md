# User Management Service

A RESTful API service for managing users built with Spring Boot 3.5.7 and PostgreSQL.

## Features

- ✅ Create, Read, Update, Delete (CRUD) operations for users
- ✅ Pagination support for listing users
- ✅ Input validation with proper error handling
- ✅ Comprehensive logging
- ✅ Environment variable configuration
- ✅ Unit tests for service and controller layers
- ✅ Clean architecture with separation of concerns

## Tech Stack

- **Java 21**
- **Spring Boot 3.5.7**
- **PostgreSQL** (Database)
- **Spring Data JPA** (ORM)
- **Lombok** (Reducing boilerplate)
- **Maven** (Build tool)
- **JUnit 5** (Testing)

## Prerequisites

- Java 21 or higher
- Maven 3.6+
- PostgreSQL 12+ (or use Docker to run PostgreSQL)
- IDE (IntelliJ IDEA, Eclipse, VS Code, etc.)

## Setup Instructions

### 1. Clone the Repository

```bash
git clone <repository-url>
cd user-management-service
```

### 2. Database Setup

#### Option A: Using Local PostgreSQL

1. Create a PostgreSQL database:

```sql
CREATE DATABASE userdb;
```

2. Update `application.properties` or set environment variables (see Configuration section)

#### Option B: Using Docker (PostgreSQL only)

```bash
docker run --name postgres-userdb \
  -e POSTGRES_PASSWORD=password \
  -e POSTGRES_DB=userdb \
  -p 5432:5432 \
  -d postgres:15
```

### 3. Configuration

The application uses environment variables for configuration. You can set them or use the default values in `application.properties`.

**Environment Variables:**

| Variable            | Description               | Default Value                             |
| ------------------- | ------------------------- | ----------------------------------------- |
| `DATABASE_URL`      | PostgreSQL connection URL | `jdbc:postgresql://localhost:5432/userdb` |
| `DATABASE_USERNAME` | Database username         | `postgres`                                |
| `DATABASE_PASSWORD` | Database password         | `password`                                |
| `SERVER_PORT`       | Server port               | `8080`                                    |
| `JPA_SHOW_SQL`      | Show SQL queries in logs  | `true`                                    |
| `JPA_DDL_AUTO`      | Hibernate DDL mode        | `update`                                  |

**Example (Windows PowerShell):**

```powershell
$env:DATABASE_URL="jdbc:postgresql://localhost:5432/userdb"
$env:DATABASE_USERNAME="postgres"
$env:DATABASE_PASSWORD="your_password"
$env:SERVER_PORT="8080"
```

**Example (Linux/Mac):**

```bash
export DATABASE_URL="jdbc:postgresql://localhost:5432/userdb"
export DATABASE_USERNAME="postgres"
export DATABASE_PASSWORD="your_password"
export SERVER_PORT="8080"
```

### 4. Build and Run

#### Using Maven:

```bash
# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

#### Using IDE:

1. Import the project as a Maven project
2. Run `UserManagementServiceApplication.java`

The application will start on `http://localhost:8080` (or the port specified in `SERVER_PORT`).

### 5. Run Tests

```bash
mvn test
```

## API Documentation

Base URL: `http://localhost:8080/api/users`

### 1. Create a New User

**Endpoint:** `POST /api/users`

**Request Body:**

```json
{
  "name": "John Doe",
  "email": "john.doe@example.com",
  "phone": "1234567890",
  "role": "USER"
}
```

**Response:** `201 Created`

```json
{
  "id": 1,
  "name": "John Doe",
  "email": "john.doe@example.com",
  "phone": "1234567890",
  "role": "USER",
  "createdAt": "2024-01-15T10:30:00",
  "modifiedAt": "2024-01-15T10:30:00"
}
```

**Example using cURL:**

```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john.doe@example.com",
    "phone": "1234567890",
    "role": "USER"
  }'
```

**Example using PowerShell:**

```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/users" `
  -Method Post `
  -ContentType "application/json" `
  -Body '{"name":"John Doe","email":"john.doe@example.com","phone":"1234567890","role":"USER"}'
```

### 2. Get All Users (with Pagination)

**Endpoint:** `GET /api/users`

**Query Parameters:**

- `page` (optional): Page number (default: 0)
- `size` (optional): Page size (default: 20)
- `sort` (optional): Sort field and direction (e.g., `name,asc`)

**Response:** `200 OK`

```json
{
  "content": [
    {
      "id": 1,
      "name": "John Doe",
      "email": "john.doe@example.com",
      "phone": "1234567890",
      "role": "USER",
      "createdAt": "2024-01-15T10:30:00",
      "modifiedAt": "2024-01-15T10:30:00"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10
  },
  "totalElements": 1,
  "totalPages": 1
}
```

**Example using cURL:**

```bash
curl -X GET "http://localhost:8080/api/users?page=0&size=10"
```

**Example using PowerShell:**

```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/users?page=0&size=10" -Method Get
```

### 3. Get User by ID

**Endpoint:** `GET /api/users/{id}`

**Response:** `200 OK`

```json
{
  "id": 1,
  "name": "John Doe",
  "email": "john.doe@example.com",
  "phone": "1234567890",
  "role": "USER",
  "createdAt": "2024-01-15T10:30:00",
  "modifiedAt": "2024-01-15T10:30:00"
}
```

**Example using cURL:**

```bash
curl -X GET http://localhost:8080/api/users/1
```

**Example using PowerShell:**

```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/users/1" -Method Get
```

### 4. Update User

**Endpoint:** `PUT /api/users/{id}`

**Request Body:**

```json
{
  "name": "Jane Doe",
  "email": "jane.doe@example.com",
  "phone": "9876543210",
  "role": "ADMIN"
}
```

**Response:** `200 OK`

```json
{
  "id": 1,
  "name": "Jane Doe",
  "email": "jane.doe@example.com",
  "phone": "9876543210",
  "role": "ADMIN",
  "createdAt": "2024-01-15T10:30:00",
  "modifiedAt": "2024-01-15T11:45:00"
}
```

**Example using cURL:**

```bash
curl -X PUT http://localhost:8080/api/users/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Jane Doe",
    "email": "jane.doe@example.com",
    "phone": "9876543210",
    "role": "ADMIN"
  }'
```

**Example using PowerShell:**

```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/users/1" `
  -Method Put `
  -ContentType "application/json" `
  -Body '{"name":"Jane Doe","email":"jane.doe@example.com","phone":"9876543210","role":"ADMIN"}'
```

### 5. Delete User

**Endpoint:** `DELETE /api/users/{id}`

**Response:** `204 No Content`

**Example using cURL:**

```bash
curl -X DELETE http://localhost:8080/api/users/1
```

**Example using PowerShell:**

```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/users/1" -Method Delete
```

## Error Responses

### Validation Error (400 Bad Request)

```json
{
  "statusCode": 400,
  "message": "Validation failed: email: must be a well-formed email address, name: must not be blank",
  "timestamp": "2024-01-15T10:30:00.000+00:00"
}
```

### User Not Found (404 Not Found)

```json
{
  "statusCode": 404,
  "message": "User with id 999 not found",
  "timestamp": "2024-01-15T10:30:00.000+00:00"
}
```

### Email Already Exists (400 Bad Request)

```json
{
  "statusCode": 400,
  "message": "Email already exists",
  "timestamp": "2024-01-15T10:30:00.000+00:00"
}
```

### Internal Server Error (500 Internal Server Error)

```json
{
  "statusCode": 500,
  "message": "An unexpected error occurred",
  "timestamp": "2024-01-15T10:30:00.000+00:00"
}
```

## Data Model

### User Entity

| Field        | Type            | Constraints                    | Description                 |
| ------------ | --------------- | ------------------------------ | --------------------------- |
| `id`         | Long            | Primary Key, Auto-generated    | Unique identifier           |
| `name`       | String          | Not Blank, Required            | User's full name            |
| `email`      | String          | Not Blank, Valid Email, Unique | User's email address        |
| `phone`      | String          | Not Blank, Required            | User's phone number         |
| `role`       | UserRole (Enum) | Required, Default: USER        | User role (USER, ADMIN)     |
| `createdAt`  | LocalDateTime   | Auto-generated                 | Creation timestamp          |
| `modifiedAt` | LocalDateTime   | Auto-updated                   | Last modification timestamp |

### UserRole Enum

- `USER` - Regular user (default)
- `ADMIN` - Administrator

## Project Structure

```
src/
├── main/
│   ├── java/com/app/usermanagementservice/
│   │   ├── controller/          # REST controllers
│   │   ├── service/             # Business logic
│   │   ├── repository/          # Data access layer
│   │   ├── model/               # Entity models
│   │   ├── dto/                 # Data Transfer Objects
│   │   └── exceptions/          # Custom exceptions and handlers
│   └── resources/
│       └── application.properties
└── test/
    └── java/com/app/usermanagementservice/
        ├── controller/           # Controller tests
        └── service/              # Service tests
```

## Testing

Run all tests:

```bash
mvn test
```

Run specific test class:

```bash
mvn test -Dtest=UserServiceTest
```

## Logging

The application uses SLF4J with Logback for logging. Log levels can be configured in `application.properties`:

```properties
logging.level.com.app.usermanagementservice=INFO
logging.level.org.springframework.web=INFO
logging.level.org.hibernate.SQL=DEBUG
```

## Deployment

### Environment Variables for Production

Set the following environment variables in your deployment platform:

```bash
DATABASE_URL=jdbc:postgresql://your-db-host:5432/userdb
DATABASE_USERNAME=your_db_username
DATABASE_PASSWORD=your_db_password
SERVER_PORT=8080
JPA_SHOW_SQL=false
JPA_DDL_AUTO=update
```

### Build JAR for Deployment

```bash
mvn clean package
```

The JAR file will be created in `target/user-management-service-0.0.1-SNAPSHOT.jar`

Run the JAR:

```bash
java -jar target/user-management-service-0.0.1-SNAPSHOT.jar
```

## Health Check

To verify the application is running, you can check:

```bash
curl http://localhost:8080/api/users
```

Or visit in browser: `http://localhost:8080/api/users`

## Notes

- The `role` field in `UserRequest` is optional. If not provided, it defaults to `USER`.
- Email addresses must be unique across all users.
- The database schema is automatically created/updated on startup (controlled by `JPA_DDL_AUTO`).
- For production, consider using database migrations (Flyway/Liquibase) instead of `update` mode.

## License

This project is created for educational/demonstration purposes.
