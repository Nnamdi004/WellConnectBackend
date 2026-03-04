# API Testing Guide

## Quick Test Flow

### 1. Start Application
```bash
mvn spring-boot:run
```

### 2. Test Admin Login
```bash
curl -X POST http://localhost:8080/api/auth/admin/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@wellconnect.com",
    "password": "admin123"
  }'
```

Save the token from response.

### 3. Create Another Admin
```bash
curl -X POST http://localhost:8080/api/admin/admins \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -d '{
    "username": "admin2",
    "email": "admin2@wellconnect.com",
    "password": "admin456"
  }'
```

### 4. Register a Therapist
```bash
curl -X POST http://localhost:8080/api/admin/therapists/register \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -d '{
    "fullName": "Dr. Sarah Johnson",
    "email": "sarah.johnson@wellconnect.com",
    "specialisation": "Anxiety and Depression",
    "password": "therapist123"
  }'
```

### 5. Test Therapist Login (ACTIVE)
```bash
curl -X POST http://localhost:8080/api/auth/therapist/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "sarah.johnson@wellconnect.com",
    "password": "therapist123"
  }'
```

Should succeed and return token.

### 6. Suspend Therapist
```bash
curl -X PUT http://localhost:8080/api/admin/therapists/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_ADMIN_TOKEN_HERE" \
  -d '{
    "status": "SUSPENDED"
  }'
```

### 7. Test Therapist Login (SUSPENDED)
```bash
curl -X POST http://localhost:8080/api/auth/therapist/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "sarah.johnson@wellconnect.com",
    "password": "therapist123"
  }'
```

Should fail with "Account is SUSPENDED" error.

### 8. Get All Therapists
```bash
curl -X GET http://localhost:8080/api/admin/therapists \
  -H "Authorization: Bearer YOUR_ADMIN_TOKEN_HERE"
```

### 9. Delete Therapist
```bash
curl -X DELETE http://localhost:8080/api/admin/therapists/1 \
  -H "Authorization: Bearer YOUR_ADMIN_TOKEN_HERE"
```

## Expected Results

✅ Admin auto-created on startup
✅ Admin can login and get JWT token
✅ Admin can create other admins
✅ Admin can register therapists with hashed passwords
✅ Therapist can login when ACTIVE
✅ Therapist login rejected when INACTIVE/SUSPENDED
✅ Admin has full CRUD on therapists
✅ RBAC enforced on all endpoints
✅ Roles stored in JWT claims

## Verification Checklist

- [ ] Default admin created on startup
- [ ] Admin login works
- [ ] Admin can create admins
- [ ] Admin can update/delete admins
- [ ] Admin can register therapists
- [ ] Passwords are hashed (check database)
- [ ] Therapist login works when ACTIVE
- [ ] Therapist login fails when SUSPENDED/INACTIVE
- [ ] JWT contains role claim
- [ ] /api/admin/** requires ADMIN role
- [ ] /api/therapist/** requires THERAPIST role
- [ ] Swagger UI shows all endpoints
