# Admin & Therapist Management API

## Overview
This implementation provides complete admin and therapist management with JWT authentication and role-based access control (RBAC).

## Features Implemented

### 1. Admin Management
- **Auto-seeded admin** on application startup
- Admin login with JWT
- Admin can create other admins
- Full CRUD operations on admin accounts

### 2. Therapist Management by Admin
- Admin registers therapists
- Password hashing with BCrypt
- Full CRUD operations on therapists
- Status management (ACTIVE, INACTIVE, SUSPENDED)

### 3. Therapist Authentication
- Therapist login with status validation
- Login rejected if status is INACTIVE or SUSPENDED
- JWT token generation with THERAPIST role

### 4. Role-Based Access Control (RBAC)
- `/api/admin/**` - Only ADMIN role
- `/api/therapist/**` - Only THERAPIST role
- `/api/auth/**` - Public access

## Default Admin Credentials
```
Email: admin@wellconnect.com
Password: admin123
```

## API Endpoints

### Admin Authentication

#### Admin Login
```http
POST /api/auth/admin/login
Content-Type: application/json

{
  "email": "admin@wellconnect.com",
  "password": "admin123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "email": "admin@wellconnect.com",
  "role": "ADMIN"
}
```

### Admin Management (Requires ADMIN role)

#### Create Admin
```http
POST /api/admin/admins
Authorization: Bearer {token}
Content-Type: application/json

{
  "username": "newadmin",
  "email": "newadmin@wellconnect.com",
  "password": "password123"
}
```

#### Get All Admins
```http
GET /api/admin/admins
Authorization: Bearer {token}
```

#### Update Admin
```http
PUT /api/admin/admins/{id}
Authorization: Bearer {token}
Content-Type: application/json

{
  "username": "updatedadmin",
  "email": "updated@wellconnect.com",
  "password": "newpassword123"
}
```

#### Delete Admin
```http
DELETE /api/admin/admins/{id}
Authorization: Bearer {token}
```

### Therapist Management (Requires ADMIN role)

#### Register Therapist
```http
POST /api/admin/therapists/register
Authorization: Bearer {token}
Content-Type: application/json

{
  "fullName": "Dr. John Smith",
  "email": "john.smith@wellconnect.com",
  "specialisation": "Cognitive Behavioral Therapy",
  "password": "therapist123"
}
```

**Response:**
```json
{
  "therapistId": 1,
  "fullName": "Dr. John Smith",
  "email": "john.smith@wellconnect.com",
  "specialisation": "Cognitive Behavioral Therapy",
  "status": "ACTIVE",
  "createdAt": "2024-01-15T10:30:00"
}
```

#### Get All Therapists
```http
GET /api/admin/therapists
Authorization: Bearer {token}
```

#### Get Therapist by ID
```http
GET /api/admin/therapists/{id}
Authorization: Bearer {token}
```

#### Update Therapist
```http
PUT /api/admin/therapists/{id}
Authorization: Bearer {token}
Content-Type: application/json

{
  "fullName": "Dr. John Smith Jr.",
  "email": "john.smith.jr@wellconnect.com",
  "specialisation": "Family Therapy",
  "status": "SUSPENDED"
}
```

#### Delete Therapist
```http
DELETE /api/admin/therapists/{id}
Authorization: Bearer {token}
```

### Therapist Authentication

#### Therapist Login
```http
POST /api/auth/therapist/login
Content-Type: application/json

{
  "email": "john.smith@wellconnect.com",
  "password": "therapist123"
}
```

**Response (Success - ACTIVE status):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "email": "john.smith@wellconnect.com",
  "role": "THERAPIST"
}
```

**Response (Error - INACTIVE/SUSPENDED):**
```json
{
  "error": "Account is SUSPENDED"
}
```

## Security Implementation

### Password Hashing
- All passwords are hashed using BCryptPasswordEncoder
- Plain text passwords are never stored in the database

### JWT Token
- Tokens contain email and role claims
- Role is prefixed with "ROLE_" for Spring Security
- Token expiration configured in application.yaml

### RBAC Rules
```java
@PreAuthorize("hasRole('ADMIN')")  // Admin-only endpoints
@PreAuthorize("hasRole('THERAPIST')")  // Therapist-only endpoints
```

### Status Validation
Therapist login validates status before issuing token:
```java
if (therapist.getStatus() != Therapist.Status.ACTIVE) {
    throw new RuntimeException("Account is " + therapist.getStatus().name());
}
```

## Database Schema

### Users Table
```sql
CREATE TABLE users (
    user_id BIGSERIAL PRIMARY KEY,
    username VARCHAR(60) NOT NULL,
    email VARCHAR(120) UNIQUE,
    password_hash TEXT NOT NULL,
    bio TEXT,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

### Therapists Table
```sql
CREATE TABLE therapists (
    therapist_id BIGSERIAL PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(120) UNIQUE NOT NULL,
    password_hash TEXT NOT NULL,
    specialisation VARCHAR(100),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

## Testing with Swagger

1. Start the application
2. Navigate to: `http://localhost:{port}/swagger-ui.html`
3. Login as admin to get JWT token
4. Click "Authorize" button and enter: `Bearer {your-token}`
5. Test all endpoints

## Error Handling

All endpoints return appropriate error messages:
- `400 Bad Request` - Validation errors
- `401 Unauthorized` - Missing or invalid token
- `403 Forbidden` - Insufficient permissions
- `404 Not Found` - Resource not found
- `500 Internal Server Error` - Server errors

## Next Steps

After completing this sprint, you can:
1. Add therapist profile endpoints
2. Implement therapist availability management
3. Add appointment booking system
4. Implement therapist-patient messaging
