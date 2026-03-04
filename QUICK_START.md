# 🚀 Quick Start Guide - Sprint 1 Day 2

## ✅ What's Been Implemented

### Admin Management
- ✅ Auto-seeded admin (admin@wellconnect.com / admin123)
- ✅ Admin login with JWT
- ✅ Admin CRUD operations
- ✅ Role-based access control

### Therapist Management
- ✅ Admin registers therapists
- ✅ BCrypt password hashing
- ✅ Therapist login with status validation
- ✅ Full CRUD by admin
- ✅ Status management (ACTIVE, INACTIVE, SUSPENDED)

### Security
- ✅ JWT authentication
- ✅ Role claims in token
- ✅ @PreAuthorize RBAC
- ✅ URL-based security
- ✅ Status validation on login

---

## 🏃 Run the Application

### 1. Prerequisites
- Java 21
- PostgreSQL running
- Maven installed

### 2. Configure Environment
Edit `.env` file:
```properties
DB_URL=jdbc:postgresql://localhost:5432/wellconnect
DB_USERNAME=your_username
DB_PASSWORD=your_password
SERVER_PORT=8080
JWT_SECRET=your-secret-key-at-least-256-bits-long
JWT_EXPIRATION=86400000
ENCRYPTION_KEY=your-encryption-key
```

### 3. Start Application
```bash
mvn clean install
mvn spring-boot:run
```

### 4. Verify Startup
Look for this log message:
```
Default admin created - Email: admin@wellconnect.com, Password: admin123
```

---

## 🧪 Test the Implementation

### Quick Test (3 minutes)

#### Step 1: Admin Login
```bash
curl -X POST http://localhost:8080/api/auth/admin/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@wellconnect.com","password":"admin123"}'
```

Copy the token from response.

#### Step 2: Register Therapist
```bash
curl -X POST http://localhost:8080/api/admin/therapists/register \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "fullName":"Dr. Sarah Johnson",
    "email":"sarah@wellconnect.com",
    "specialisation":"CBT",
    "password":"therapist123"
  }'
```

#### Step 3: Therapist Login (Should Succeed)
```bash
curl -X POST http://localhost:8080/api/auth/therapist/login \
  -H "Content-Type: application/json" \
  -d '{"email":"sarah@wellconnect.com","password":"therapist123"}'
```

#### Step 4: Suspend Therapist
```bash
curl -X PUT http://localhost:8080/api/admin/therapists/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_ADMIN_TOKEN" \
  -d '{"status":"SUSPENDED"}'
```

#### Step 5: Therapist Login (Should Fail)
```bash
curl -X POST http://localhost:8080/api/auth/therapist/login \
  -H "Content-Type: application/json" \
  -d '{"email":"sarah@wellconnect.com","password":"therapist123"}'
```

Expected error: `"Account is SUSPENDED"`

---

## 📊 Access Swagger UI

1. Open browser: `http://localhost:8080/swagger-ui.html`
2. Click "Authorize" button
3. Login as admin to get token
4. Enter: `Bearer YOUR_TOKEN`
5. Test all endpoints interactively

---

## 🔍 Verify Database

### Check Admin Created
```sql
SELECT * FROM users WHERE role = 'ADMIN';
```

Expected: 1 row with email `admin@wellconnect.com`

### Check Password Hashed
```sql
SELECT password_hash FROM users WHERE email = 'admin@wellconnect.com';
```

Expected: BCrypt hash starting with `$2a$` or `$2b$`

### Check Therapist Created
```sql
SELECT * FROM therapists;
```

Expected: Therapist with status 'ACTIVE'

---

## 📝 All Endpoints

### Public (No Auth Required)
- `POST /api/auth/admin/login` - Admin login
- `POST /api/auth/therapist/login` - Therapist login

### Admin Only (Requires ADMIN role)
- `POST /api/admin/admins` - Create admin
- `GET /api/admin/admins` - List admins
- `PUT /api/admin/admins/{id}` - Update admin
- `DELETE /api/admin/admins/{id}` - Delete admin
- `POST /api/admin/therapists/register` - Register therapist
- `GET /api/admin/therapists` - List therapists
- `GET /api/admin/therapists/{id}` - Get therapist
- `PUT /api/admin/therapists/{id}` - Update therapist
- `DELETE /api/admin/therapists/{id}` - Delete therapist

---

## 🎯 Key Features to Test

### 1. Password Hashing ✓
- Register therapist with plain password
- Check database: password_hash is BCrypt hash
- Login works with plain password

### 2. Status Validation ✓
- Therapist with ACTIVE status can login
- Therapist with SUSPENDED status cannot login
- Therapist with INACTIVE status cannot login

### 3. RBAC ✓
- Admin can access /api/admin/**
- Therapist cannot access /api/admin/**
- Non-authenticated users cannot access protected routes

### 4. JWT Claims ✓
- Token contains email (subject)
- Token contains role (claim)
- Role is used for authorization

### 5. Auto-Seeding ✓
- Admin created on first startup
- Admin not duplicated on restart
- Admin can login immediately

---

## 📚 Documentation Files

- `ADMIN_THERAPIST_API.md` - Complete API documentation
- `TESTING_GUIDE.md` - Detailed testing instructions
- `POSTMAN_COLLECTION.md` - Postman/cURL examples
- `AUTHENTICATION_FLOW.md` - Flow diagrams
- `SPRINT1_DAY2_SUMMARY.md` - Implementation summary

---

## 🐛 Troubleshooting

### Admin Not Created
- Check logs for errors
- Verify database connection
- Check if email already exists

### 401 Unauthorized
- Token expired (default: 24 hours)
- Token invalid or malformed
- Login again to get fresh token

### 403 Forbidden
- Wrong role (e.g., therapist trying to access admin endpoint)
- Use correct token for the endpoint

### Therapist Login Fails
- Check status in database
- Must be ACTIVE to login
- Verify password is correct

---

## ✨ Next Steps

After verifying everything works:

1. ✅ Admin management - DONE
2. ✅ Therapist registration - DONE
3. ✅ Therapist login with status - DONE
4. ✅ RBAC implementation - DONE
5. 🔜 Therapist profile endpoints
6. 🔜 Appointment booking system
7. 🔜 Patient management
8. 🔜 Session notes

---

## 🎉 Success Criteria

- [x] Default admin auto-created
- [x] Admin can login
- [x] Admin can create admins
- [x] Admin can register therapists
- [x] Passwords are hashed
- [x] Therapist can login when ACTIVE
- [x] Therapist login fails when SUSPENDED/INACTIVE
- [x] RBAC enforced on all endpoints
- [x] JWT contains role claims
- [x] Swagger UI accessible

**All Sprint 1 Day 2 requirements completed! 🚀**
