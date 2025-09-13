# JWT MongoDB User Authentication

A Spring Boot application that provides JWT-based authentication with MongoDB as the database.

## Features

- User registration and login
- JWT token generation and validation
- Password encryption using BCrypt
- MongoDB integration
- Input validation
- Protected endpoints

## Prerequisites

- Java 21
- MongoDB running on localhost:27017
- Maven

## Getting Started

1. **Start MongoDB**
   ```bash
   # Make sure MongoDB is running on localhost:27017
   mongod
   ```

2. **Run the application**
   ```bash
   ./mvnw spring-boot:run
   ```

3. **The application will start on port 8080**

## API Endpoints

### Public Endpoints

#### Register a new user
```http
POST /auth/register
Content-Type: application/json

{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123"
}
```

#### Login
```http
POST /auth/login
Content-Type: application/json

{
    "username": "testuser",
    "password": "password123"
}
```

Response:
```json
{
    "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

#### Test endpoint
```http
GET /auth/test
```

### Protected Endpoints

#### Protected endpoint (requires JWT token)
```http
GET /auth/protected
Authorization: Bearer <your-jwt-token>
```

## Configuration

The application uses the following configuration in `application.properties`:

- **MongoDB URI**: `mongodb://localhost:27017/userdb`
- **JWT Secret**: Base64 encoded secret key
- **JWT Expiration**: 1 hour (3600000 ms)

## Security

- Passwords are encrypted using BCrypt
- JWT tokens are signed with HS256 algorithm
- Protected endpoints require valid JWT token in Authorization header
- Input validation for user registration

## Database

The application creates a `userdb` database in MongoDB with a `users` collection.

User document structure:
```json
{
    "_id": "ObjectId",
    "username": "string (unique)",
    "email": "string (unique)",
    "password": "string (encrypted)",
    "role": "string (default: USER)"
}
```

## Testing with curl

1. **Register a user:**
```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","email":"test@example.com","password":"password123"}'
```

2. **Login:**
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"password123"}'
```

3. **Access protected endpoint:**
```bash
curl -X GET http://localhost:8080/auth/protected \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE"
```