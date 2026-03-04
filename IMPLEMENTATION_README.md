# 🏥 WellConnect Backend - Sprint 1 Day 2 Complete

## 📋 Implementation Overview

This implementation provides a complete **Admin and Therapist Management System** with JWT authentication, role-based access control, and secure password handling.

---

## 🎯 Features Delivered

### ✅ Admin Management
- **Auto-Seeded Admin**: Default admin created on startup
- **Admin Authentication**: JWT-based login
- **Admin CRUD**: Create, read, update, delete admin accounts
- **Role Management**: Admin role stored in JWT claims

### ✅ Therapist Management
- **Registration by Admin**: Only admins can register therapists
- **Password Security**: BCrypt hashing (never store plain text)
- **Status Management**: ACTIVE, INACTIVE, SUSPENDED
- **Full CRUD**: Complete therapist lifecycle management

### ✅ Therapist Authentication
- **Login Endpoint**: POST /api/auth/therapist/login
- **Status Validation**: Login rejected if not ACTIVE
- **JWT Token**: Contains email and THERAPIST role

### ✅ Security & RBAC
- **Method-Level**: @PreAuthorize annotations
- **URL-Level**: SecurityFilterChain rules
- **Role-Based**: ADMIN and THERAPIST roles
- **JWT Claims**: Role stored in token for authorization

---

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                     WellConnect Backend                      │
├─────────────────────────────────────────────────────────────┤
│                                                               │
│  ┌──────────────┐      ┌──────────────┐                     │
│  │   Admin      │      │  Therapist   │                     │
│  │  Controller  │      │  Controller  │                     │
│  └──────┬───────┘      └──────┬───────┘                     │
│         │                     │                              │
│         ▼                     ▼                              │
│  ┌──────────────┐      ┌──────────────┐                     │
│  │   Admin      │      │  Therapist   │                     │
│  │   Service    │      │   Service    │                     │
│  └──────┬───────┘      └──────┬───────┘                     │
│         │                     │                              │
│         ▼                     ▼                              │
│  ┌──────────────┐      ┌──────────────┐                     │
│  │    User      │      │  Therapist   │                     │
│  │  Repository  │      │  Repository  │                     │
│  └──────┬───────┘      └──────┬───────┘                     │
│         │                     │                              │
│         └──────────┬──────────┘                              │
│                    ▼                                         │
│            ┌──────────────┐                                  │
│            │  PostgreSQL  │                                  │
│            │   Database   │                                  │
│            └──────────────┘                                  │
│                                                               │
├─────────────────────────────────────────────────────────────┤
│                    Security Layer                            │
├─────────────────────────────────────────────────────────────┤
│  JWT Filter → Role Validation → @PreAuthorize → Endpoint    │
└─────────────────────────────────────────────────────────────┘
```

---

## 📁 Project Structure

```
src/main/java/com/alu/wellconnect/
├── config/
│   ├── DataSeeder.java              ← Auto-seed admin
│   ├── OpenApiConfig.java           ← Swagger config
│   └── SecurityConfig.java          ← Security + RBAC
├── controller/
│   ├── AdminController.java         ← Admin endpoints
│   ├── AuthController.java          ← User auth
│   └── TherapistController.java     ← Therapist endpoints
├── dto/
│   ├── AdminRegisterRequest.java    ← Admin registration
│   ├── AuthResponse.java            ← Login response
│   ├── LoginRequest.java            ← Login payload
│   ├── RegisterRequest.java         ← User registration
│   ├── TherapistRegisterRequest.java ← Therapist registration
│   ├── TherapistResponse.java       ← Therapist response
│   └── TherapistUpdateRequest.java  ← Therapist update
├── entity/
│   ├── Therapist.java               ← Therapist entity
│   └── User.java                    ← User entity (updated)
├── exception/
│   └── GlobalExceptionHandler.java  ← Error handling
├── repository/
│   ├── TherapistRepository.java     ← Therapist data access
│   └── UserRepository.java          ← User data access
├── security/
│   └── JwtAuthFilter.java           ← JWT validation
├── service/
│   ├── AdminService.java            ← Admin business logic
│   ├── AuthService.java             ← User auth logic
│   └── TherapistService.java        ← Therapist business logic
├── util/
│   ├── EncryptionUtil.java          ← Encryption utilities
│   └── JwtUtil.java                 ← JWT utilities
└── WellconnectApplication.java      ← Main application
```

---

## 🗄️ Database Schema

### Users Table
```sql
CREATE TABLE users (
    user_id         BIGSERIAL PRIMARY KEY,
    username        VARCHAR(60) NOT NULL,
    email           VARCHAR(120) UNIQUE,
    password_hash   TEXT NOT NULL,
    bio             TEXT,
    role            VARCHAR(20) NOT NULL DEFAULT 'USER',
    status          VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

**Roles**: USER, THERAPIST, ADMIN  
**Status**: ACTIVE, SUSPENDED

### Therapists Table
```sql
CREATE TABLE therapists (
    therapist_id    BIGSERIAL PRIMARY KEY,
    full_name       VARCHAR(100) NOT NULL,
    email           VARCHAR(120) UNIQUE NOT NULL,
    password_hash   TEXT NOT NULL,
    specialisation  VARCHAR(100),
    status          VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

**Status**: ACTIVE, INACTIVE, SUSPENDED

---

## 🔐 Security Implementation

### 1. Password Hashing
```java
// Registration
String hashedPassword = passwordEncoder.encode(plainPassword);

// Login
boolean matches = passwordEncoder.matches(plainPassword, hashedPassword);
```

### 2. JWT Token Generation
```java
String token = jwtUtil.generateToken(email, role);
// Token contains:
// - Subject: email
// - Claim: role
// - Expiration: 24 hours (configurable)
```

### 3. RBAC Configuration
```java
// URL-based
.requestMatchers("/api/admin/**").hasRole("ADMIN")
.requestMatchers("/api/therapist/**").hasRole("THERAPIST")

// Method-based
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<TherapistResponse> registerTherapist(...)
```

### 4. Status Validation
```java
if (therapist.getStatus() != Therapist.Status.ACTIVE) {
    throw new RuntimeException("Account is " + therapist.getStatus().name());
}
```

---

## 🚀 Getting Started

### 1. Clone and Configure
```bash
cd WellConnectBackend
cp .env.example .env
# Edit .env with your database credentials
```

### 2. Run Application
```bash
mvn clean install
mvn spring-boot:run
```

### 3. Verify Admin Created
Check logs for:
```
Default admin created - Email: admin@wellconnect.com, Password: admin123
```

### 4. Access Swagger UI
```
http://localhost:8080/swagger-ui.html
```

### 5. Test Admin Login
```bash
curl -X POST http://localhost:8080/api/auth/admin/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@wellconnect.com","password":"admin123"}'
```

---

## 📚 API Documentation

### Default Credentials
```
Admin Email: admin@wellconnect.com
Admin Password: admin123
```

### Endpoints Summary

| Method | Endpoint | Role | Description |
|--------|----------|------|-------------|
| POST | `/api/auth/admin/login` | Public | Admin login |
| POST | `/api/auth/therapist/login` | Public | Therapist login |
| POST | `/api/admin/admins` | ADMIN | Create admin |
| GET | `/api/admin/admins` | ADMIN | List admins |
| PUT | `/api/admin/admins/{id}` | ADMIN | Update admin |
| DELETE | `/api/admin/admins/{id}` | ADMIN | Delete admin |
| POST | `/api/admin/therapists/register` | ADMIN | Register therapist |
| GET | `/api/admin/therapists` | ADMIN | List therapists |
| GET | `/api/admin/therapists/{id}` | ADMIN | Get therapist |
| PUT | `/api/admin/therapists/{id}` | ADMIN | Update therapist |
| DELETE | `/api/admin/therapists/{id}` | ADMIN | Delete therapist |

---

## 🧪 Testing

### Quick Test Flow
1. ✅ Admin login → Get token
2. ✅ Create admin → Verify in DB
3. ✅ Register therapist → Check password hashed
4. ✅ Therapist login (ACTIVE) → Success
5. ✅ Suspend therapist → Update status
6. ✅ Therapist login (SUSPENDED) → Fail
7. ✅ Test RBAC → Therapist cannot access admin endpoints

### Test Files
- `TESTING_GUIDE.md` - Detailed test scenarios
- `POSTMAN_COLLECTION.md` - Postman examples
- `QUICK_START.md` - Quick start guide

---

## 📖 Documentation Files

| File | Description |
|------|-------------|
| `ADMIN_THERAPIST_API.md` | Complete API documentation |
| `AUTHENTICATION_FLOW.md` | Flow diagrams and security layers |
| `POSTMAN_COLLECTION.md` | Postman/cURL examples |
| `TESTING_GUIDE.md` | Testing instructions |
| `SPRINT1_DAY2_SUMMARY.md` | Implementation summary |
| `QUICK_START.md` | Quick start guide |

---

## ✅ Sprint 1 Day 2 Checklist

- [x] Admin auto-seeded on startup
- [x] Admin login with JWT
- [x] Admin can create other admins
- [x] Admin CRUD operations
- [x] Admin registers therapists
- [x] Password hashing with BCrypt
- [x] Therapist saved to database
- [x] Default status: ACTIVE
- [x] Therapist login endpoint
- [x] Status validation on login
- [x] Reject INACTIVE/SUSPENDED logins
- [x] RBAC with @PreAuthorize
- [x] /api/admin/** locked to ADMIN
- [x] /api/therapist/** locked to THERAPIST
- [x] Role claims in JWT
- [x] Swagger documentation

**All requirements completed! 🎉**

---

## 🔧 Technologies Used

- **Java 21**
- **Spring Boot 3.3.5**
- **Spring Security** - Authentication & Authorization
- **Spring Data JPA** - Database access
- **PostgreSQL** - Database
- **JWT (jjwt 0.11.5)** - Token generation
- **BCrypt** - Password hashing
- **Lombok** - Boilerplate reduction
- **Swagger/OpenAPI** - API documentation
- **Maven** - Build tool

---

## 🎯 Key Achievements

1. ✅ **Secure Authentication**: JWT-based with role claims
2. ✅ **Password Security**: BCrypt hashing, never plain text
3. ✅ **RBAC**: Multi-layer authorization (URL + Method)
4. ✅ **Status Validation**: Business logic enforcement
5. ✅ **Auto-Seeding**: Zero-config admin setup
6. ✅ **Clean Architecture**: Separation of concerns
7. ✅ **API Documentation**: Swagger UI ready
8. ✅ **Production Ready**: Error handling, validation

---

## 🚀 Next Sprint Tasks

- [ ] Therapist profile management
- [ ] Patient registration and management
- [ ] Appointment booking system
- [ ] Session notes and records
- [ ] Therapist availability calendar
- [ ] Patient-therapist messaging

---

## 📞 Support

For questions or issues:
1. Check documentation files
2. Review Swagger UI
3. Check application logs
4. Verify database connection

---

**Built with ❤️ for WellConnect Mental Health Platform**
