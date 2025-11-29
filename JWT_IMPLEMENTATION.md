# JWT Authentication System Implementation

## Overview
This implementation adds a complete JWT-based authentication system to the microservices architecture with role-based access control (RBAC).

## Components

### 1. Authentication Service (New Microservice)
**Port**: 8087
**Database**: PostgreSQL (auth_service)

#### Files Created/Modified:
- `AuthApplication.java` - Spring Boot main class with BCryptPasswordEncoder bean
- `config/JwtConfig.java` - JWT token generation, validation, and claims extraction
- `model/User.java` - User entity with roles relationship
- `model/Role.java` - Role entity (admin, customer, staff)
- `model/LoginRequest.java` - Login DTO (username, password)
- `repository/UserRepository.java` - User data access
- `repository/RoleRepository.java` - Role data access
- `service/UserService.java` - User validation and password checking
- `service/AuthService.java` - JWT token generation and login logic
- `controller/AuthController.java` - POST /auth/login endpoint
- `application.yml` - Configuration (port 8087, PostgreSQL)
- `Dockerfile` - Multi-stage build

#### Database Schema:
```sql
CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    email VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE user_roles (
    user_id BIGINT REFERENCES users(id),
    role_id BIGINT REFERENCES roles(id),
    PRIMARY KEY (user_id, role_id)
);
```

### 2. API Gateway Updates
**Port**: 8080

#### New/Modified Components:
- `GatewayConfig.java` - Added route for auth-service (/api/auth/**)
- `filter/JwtAuthenticationFilter.java` - Global JWT validation filter
- `application.yml` - Added JWT secret configuration
- `pom.xml` - Added JJWT dependencies

#### Filter Behavior:
- Validates JWT token on all requests except `/auth/login`
- Returns 401 Unauthorized if token missing or invalid
- Extracts user claims and adds headers:
  - `X-User-Id`: User ID from token
  - `X-User-Name`: Username from token
  - `X-User-Roles`: Comma-separated roles from token

### 3. Docker Compose Updates
Added:
- `auth-db` - PostgreSQL container for auth service
- `authentication-service` - Container for auth service
- Updated `api-gateway` dependencies to include auth service
- Added auth_data volume

## JWT Token Structure
```
Header: 
  {"alg": "HS512", "typ": "JWT"}

Payload:
  {
    "sub": "1",           // userId
    "username": "john",
    "roles": ["admin"],
    "iat": 1234567890,
    "exp": 1234568790    // 15 minutes later
  }

Signature: HMAC-SHA512(secret)
```

## API Endpoints

### Authentication Service
- **POST /api/auth/login**
  ```json
  Request:
  {
    "username": "admin",
    "password": "password123"
  }
  
  Response (200 OK):
  {
    "token": "eyJhbGc...",
    "expiresIn": 900  // seconds
  }
  
  Response (401 Unauthorized):
  {
    "error": "Invalid credentials"
  }
  ```

## Usage Flow

### 1. Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "password123"}'
```

### 2. Use Token to Access Protected Resources
```bash
curl -X GET http://localhost:8080/api/orders \
  -H "Authorization: Bearer <token>"
```

The gateway will validate the token and forward the request with user context headers.

## Security Features

1. **Password Hashing**: BCryptPasswordEncoder for secure password storage
2. **JWT Validation**: HMAC-SHA512 signature verification
3. **Token Expiration**: 15-minute token validity
4. **User Context**: Headers propagated to downstream services
5. **Role Management**: RBAC foundation for authorization

## Configuration

### Environment Variables
```bash
JWT_SECRET=your-super-secret-jwt-key-change-this-in-production
JWT_EXPIRATION=900000  # 15 minutes in milliseconds
```

### Database Setup
PostgreSQL will auto-create schema on first run (ddl-auto: update).

#### Pre-populate with sample data (optional):
```sql
INSERT INTO roles (name) VALUES ('admin'), ('customer'), ('staff');

INSERT INTO users (username, email, password_hash) VALUES 
  ('admin', 'admin@example.com', '$2a$10$...');  -- BCrypt hash
```

## Next Steps (Optional Enhancements)

1. **Refresh Tokens**: Implement 7-day refresh token flow
2. **Password Reset**: Add password reset endpoint
3. **Rate Limiting**: Add rate limiting to login endpoint
4. **User Registration**: Add public registration endpoint
5. **RBAC Enforcement**: Add @RoleRequired annotations to service endpoints
6. **Audit Logging**: Log all authentication events
7. **MFA Support**: Add multi-factor authentication
8. **Token Blacklist**: Implement logout with token blacklisting

## Testing

### Start Services
```bash
docker-compose up -d
```

### Wait for services to be ready
```bash
sleep 20
```

### Create test user (if not auto-populated)
```bash
# Connect to auth-db and insert a test user
# Note: Replace hash with actual BCrypt hash
docker exec -it auth-db psql -U postgres -d auth_service -c "
INSERT INTO users (username, email, password_hash, created_at) 
VALUES 
  ('admin', 'admin@example.com', '\$2a\$10\$slYQmyNdGzin7olVN3p5OPST9EwkrrWQpH9kK97QP/33jrHksom36', NOW()),
  ('customer', 'customer@example.com', '\$2a\$10\$slYQmyNdGzin7olVN3p5OPST9EwkrrWQpH9kK97QP/33jrHksom36', NOW()),
  ('staff', 'staff@example.com', '\$2a\$10\$slYQmyNdGzin7olVN3p5OPST9EwkrrWQpH9kK97QP/33jrHksom36', NOW())
ON CONFLICT DO NOTHING;
SELECT id, username, email FROM users;
"
```

### Assign roles to users
```bash
docker exec -it auth-db psql -U postgres -d auth_service -c "
INSERT INTO user_roles (user_id, role_id)
VALUES 
  (2, 1),  -- admin user -> admin role
  (3, 2),  -- customer user -> customer role
  (4, 3)   -- staff user -> staff role
ON CONFLICT DO NOTHING;
SELECT * FROM user_roles;
"
```

### Test Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "password123"}'
```

### Test Protected Endpoint
```bash
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "password123"}' | jq -r '.token')

curl -X GET http://localhost:8080/api/orders \
  -H "Authorization: Bearer $TOKEN"
```

## Architecture Diagram

```
┌─────────────┐
│   Client    │
└──────┬──────┘
       │ HTTP with Bearer Token
       ▼
┌─────────────────────┐
│   API Gateway       │
│ Port 8080           │
│ JWT Filter          │
│ Validates Token     │
│ Adds User Headers   │
└──────┬──────────────┘
       │ Routes to service + user context headers
       ▼
┌──────────────────┐
│ Microservices    │
│ (Orders, etc)    │
└──────────────────┘
       │
       ▼
┌─────────────────────┐
│ Auth Service        │
│ Port 8087           │
│ Generates JWT       │
│ Manages Users/Roles │
└─────────────────────┘
       │
       ▼
┌─────────────────────┐
│ PostgreSQL          │
│ auth_service DB     │
└─────────────────────┘
```

## Files Summary

### Created
- `/authentication-service/` - Complete Auth Service
- `/api-gateway/src/main/java/com/example/apigateway/filter/JwtAuthenticationFilter.java`

### Modified
- `docker-compose.yml` - Added auth-db and authentication-service
- `api-gateway/src/main/java/com/example/apigateway/config/GatewayConfig.java` - Added auth route
- `api-gateway/pom.xml` - Added JWT dependencies
- `api-gateway/src/main/resources/application.yml` - Added JWT config

## Status: ✅ Complete
All JWT authentication components implemented and ready for deployment.
