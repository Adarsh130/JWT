# JWT MongoDB RBAC API Documentation

## Overview
This application implements Role-Based Access Control (RBAC) with JWT authentication using Spring Boot and MongoDB. It supports two main roles: **USER** and **ADMIN** with specific permissions for different endpoints.

## Roles and Permissions

### USER Role
- Can access `/user/**` endpoints
- Can access general authenticated endpoints
- Default role for new registrations

### ADMIN Role
- Can access `/admin/**` endpoints
- Can also access `/user/**` endpoints (inherits USER permissions)
- Can manage users and view system statistics

## API Endpoints

### Authentication Endpoints (`/auth/**`)
All authentication endpoints are publicly accessible.

#### 1. Register User
```http
POST /auth/register
Content-Type: application/json

{
    "username": "john_doe",
    "email": "john@example.com",
    "password": "password123"
}
```

**Response:**
```json
{
    "message": "User registered successfully",
    "userId": "user_id_here",
    "roles": ["USER"]
}
```

#### 2. Register Admin
```http
POST /auth/register-admin
Content-Type: application/json

{
    "username": "admin_user",
    "email": "admin@example.com",
    "password": "admin123"
}
```

**Response:**
```json
{
    "message": "Admin registered successfully",
    "userId": "admin_id_here",
    "roles": ["ADMIN", "USER"]
}
```

#### 3. Login
```http
POST /auth/login
Content-Type: application/json

{
    "username": "john_doe",
    "password": "password123"
}
```

**Response:**
```json
{
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "username": "john_doe",
    "roles": ["USER"]
}
```

### User Endpoints (`/user/**`)
Requires USER or ADMIN role. Include JWT token in Authorization header: `Bearer <token>`

#### 1. User Dashboard
```http
GET /user/dashboard
Authorization: Bearer <token>
```

#### 2. Get User Profile
```http
GET /user/profile
Authorization: Bearer <token>
```

#### 3. Update Profile
```http
PUT /user/profile
Authorization: Bearer <token>
Content-Type: application/json

{
    "email": "newemail@example.com"
}
```

#### 4. Get User Settings
```http
GET /user/settings
Authorization: Bearer <token>
```

### Admin Endpoints (`/admin/**`)
Requires ADMIN role. Include JWT token in Authorization header: `Bearer <token>`

#### 1. Admin Dashboard
```http
GET /admin/dashboard
Authorization: Bearer <token>
```

#### 2. Get All Users
```http
GET /admin/users
Authorization: Bearer <token>
```

#### 3. Get User by ID
```http
GET /admin/users/{userId}
Authorization: Bearer <token>
```

#### 4. Delete User
```http
DELETE /admin/users/{userId}
Authorization: Bearer <token>
```

#### 5. Get System Statistics
```http
GET /admin/stats
Authorization: Bearer <token>
```

#### 6. Promote User to Admin
```http
POST /admin/promote/{userId}
Authorization: Bearer <token>
```

### Test Endpoints (`/test/**`)
For testing different access levels.

#### 1. Public Endpoint (No Authentication)
```http
GET /test/public
```

#### 2. Authenticated Endpoint
```http
GET /test/authenticated
Authorization: Bearer <token>
```

#### 3. User Only Endpoint
```http
GET /test/user-only
Authorization: Bearer <token>
```

#### 4. Admin Only Endpoint
```http
GET /test/admin-only
Authorization: Bearer <token>
```

#### 5. User or Admin Endpoint
```http
GET /test/user-or-admin
Authorization: Bearer <token>
```

## Security Configuration

### Route-Based Security
- `/auth/**` - Public access
- `/test/public` - Public access
- `/admin/**` - ADMIN role required
- `/user/**` - USER or ADMIN role required
- All other routes - Authentication required

### Method-Level Security
Controllers use `@PreAuthorize` annotations for additional security:
- `@PreAuthorize("hasRole('ADMIN')")` - Admin only
- `@PreAuthorize("hasRole('USER')")` - User only
- `@PreAuthorize("hasAnyRole('USER', 'ADMIN')")` - User or Admin

## JWT Token Structure
The JWT token includes:
- Subject: username
- Roles: array of user roles
- Issued at: token creation time
- Expiration: 1 hour from creation

## Error Responses

### 401 Unauthorized
```json
{
    "error": "Invalid username or password"
}
```

### 403 Forbidden
```json
{
    "timestamp": "2024-01-01T12:00:00.000+00:00",
    "status": 403,
    "error": "Forbidden",
    "path": "/admin/dashboard"
}
```

### 400 Bad Request
```json
{
    "error": "Username already exists"
}
```

## Testing the RBAC System

### 1. Register a regular user
```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","email":"test@example.com","password":"password123"}'
```

### 2. Register an admin user
```bash
curl -X POST http://localhost:8080/auth/register-admin \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","email":"admin@example.com","password":"admin123"}'
```

### 3. Login and get token
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"password123"}'
```

### 4. Test user endpoints
```bash
curl -X GET http://localhost:8080/user/dashboard \
  -H "Authorization: Bearer <your_token_here>"
```

### 5. Test admin endpoints (should fail with user token)
```bash
curl -X GET http://localhost:8080/admin/dashboard \
  -H "Authorization: Bearer <user_token_here>"
```

### 6. Test admin endpoints (should succeed with admin token)
```bash
curl -X GET http://localhost:8080/admin/dashboard \
  -H "Authorization: Bearer <admin_token_here>"
```

## Database Schema

### User Collection
```json
{
    "_id": "ObjectId",
    "username": "string (unique)",
    "email": "string (unique)",
    "password": "string (hashed)",
    "roles": ["USER", "ADMIN"]
}
```

## Configuration
Update `application.properties` for your MongoDB connection:
```properties
spring.data.mongodb.uri=mongodb://localhost:27017/userdb
spring.data.mongodb.database=userdb
jwt.secret=your_base64_encoded_secret
jwt.expiration=3600000
```