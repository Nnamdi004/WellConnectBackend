# Authentication API Documentation

## Base URL
```
http://localhost:8080/api/auth
```

## Endpoints

### 1. Register User

**POST** `/api/auth/register`

**Request Body:**
```json
{
  "username": "HealingVoice_2024",
  "email": "user@example.com",
  "password": "securePassword123"
}
```

**Optional Registration (Anonymous):**
```json
{
  "password": "securePassword123"
}
```
*Note: If username is not provided, it will be auto-generated (e.g., "BraveHeart_7823")*
*Note: Email is optional for anonymous registration*

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "HealingVoice_2024",
  "role": "USER"
}
```

**Error Response (400 Bad Request):**
```json
{
  "error": "Email already exists"
}
```

---

### 2. Login User

**POST** `/api/auth/login`

**Request Body:**
```json
{
  "email": "user@example.com",
  "password": "securePassword123"
}
```

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "HealingVoice_2024",
  "role": "USER"
}
```

**Error Responses:**
```json
{
  "error": "Invalid credentials"
}
```
```json
{
  "error": "Account suspended"
}
```

---

## Using the JWT Token

Include the token in the Authorization header for protected endpoints:

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

---

## Security Features

1. **Email Encryption**: All emails are encrypted at rest using AES encryption
2. **Password Hashing**: Passwords are hashed using BCrypt (never stored in plain text)
3. **JWT Authentication**: Stateless authentication with 24-hour token expiration
4. **Anonymous Registration**: Users can register without providing email
5. **Auto-generated Usernames**: Privacy-focused pseudonyms generated automatically

---

## Configuration Required

Update `application.yaml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/wellconnect
    username: your_db_username
    password: your_db_password

jwt:
  secret: your-256-bit-secret-key-change-this-in-production
  expiration: 86400000

encryption:
  key: your-16-character-key
```
