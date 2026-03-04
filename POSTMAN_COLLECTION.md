# Postman Collection - WellConnect Admin & Therapist API

## Environment Variables
Create these variables in Postman:
- `baseUrl`: http://localhost:8080
- `adminToken`: (will be set after admin login)
- `therapistToken`: (will be set after therapist login)

---

## 1. Admin Login
**POST** `{{baseUrl}}/api/auth/admin/login`

**Body (JSON):**
```json
{
  "email": "admin@wellconnect.com",
  "password": "admin123"
}
```

**Test Script (save token):**
```javascript
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

var jsonData = pm.response.json();
pm.environment.set("adminToken", jsonData.token);
```

---

## 2. Create Another Admin
**POST** `{{baseUrl}}/api/admin/admins`

**Headers:**
- `Authorization`: Bearer {{adminToken}}

**Body (JSON):**
```json
{
  "username": "admin2",
  "email": "admin2@wellconnect.com",
  "password": "SecurePass123!"
}
```

---

## 3. Get All Admins
**GET** `{{baseUrl}}/api/admin/admins`

**Headers:**
- `Authorization`: Bearer {{adminToken}}

---

## 4. Update Admin
**PUT** `{{baseUrl}}/api/admin/admins/2`

**Headers:**
- `Authorization`: Bearer {{adminToken}}

**Body (JSON):**
```json
{
  "username": "admin2_updated",
  "email": "admin2.updated@wellconnect.com"
}
```

---

## 5. Register Therapist (by Admin)
**POST** `{{baseUrl}}/api/admin/therapists/register`

**Headers:**
- `Authorization`: Bearer {{adminToken}}

**Body (JSON):**
```json
{
  "fullName": "Dr. Sarah Johnson",
  "email": "sarah.johnson@wellconnect.com",
  "specialisation": "Cognitive Behavioral Therapy",
  "password": "Therapist123!"
}
```

---

## 6. Register Multiple Therapists
**POST** `{{baseUrl}}/api/admin/therapists/register`

**Headers:**
- `Authorization`: Bearer {{adminToken}}

**Body (JSON) - Therapist 2:**
```json
{
  "fullName": "Dr. Michael Chen",
  "email": "michael.chen@wellconnect.com",
  "specialisation": "Anxiety and Depression",
  "password": "Therapist456!"
}
```

**Body (JSON) - Therapist 3:**
```json
{
  "fullName": "Dr. Emily Rodriguez",
  "email": "emily.rodriguez@wellconnect.com",
  "specialisation": "Family Therapy",
  "password": "Therapist789!"
}
```

---

## 7. Get All Therapists
**GET** `{{baseUrl}}/api/admin/therapists`

**Headers:**
- `Authorization`: Bearer {{adminToken}}

---

## 8. Get Therapist by ID
**GET** `{{baseUrl}}/api/admin/therapists/1`

**Headers:**
- `Authorization`: Bearer {{adminToken}}

---

## 9. Update Therapist
**PUT** `{{baseUrl}}/api/admin/therapists/1`

**Headers:**
- `Authorization`: Bearer {{adminToken}}

**Body (JSON):**
```json
{
  "fullName": "Dr. Sarah Johnson-Smith",
  "specialisation": "CBT and Mindfulness",
  "status": "ACTIVE"
}
```

---

## 10. Suspend Therapist
**PUT** `{{baseUrl}}/api/admin/therapists/1`

**Headers:**
- `Authorization`: Bearer {{adminToken}}

**Body (JSON):**
```json
{
  "status": "SUSPENDED"
}
```

---

## 11. Therapist Login (ACTIVE)
**POST** `{{baseUrl}}/api/auth/therapist/login`

**Body (JSON):**
```json
{
  "email": "sarah.johnson@wellconnect.com",
  "password": "Therapist123!"
}
```

**Test Script (save token):**
```javascript
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

var jsonData = pm.response.json();
pm.environment.set("therapistToken", jsonData.token);
```

**Expected Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "email": "sarah.johnson@wellconnect.com",
  "role": "THERAPIST"
}
```

---

## 12. Therapist Login (SUSPENDED) - Should Fail
**POST** `{{baseUrl}}/api/auth/therapist/login`

**Body (JSON):**
```json
{
  "email": "sarah.johnson@wellconnect.com",
  "password": "Therapist123!"
}
```

**Expected Response (Error):**
```json
{
  "error": "Account is SUSPENDED"
}
```

---

## 13. Reactivate Therapist
**PUT** `{{baseUrl}}/api/admin/therapists/1`

**Headers:**
- `Authorization`: Bearer {{adminToken}}

**Body (JSON):**
```json
{
  "status": "ACTIVE"
}
```

---

## 14. Delete Therapist
**DELETE** `{{baseUrl}}/api/admin/therapists/1`

**Headers:**
- `Authorization`: Bearer {{adminToken}}

---

## 15. Delete Admin
**DELETE** `{{baseUrl}}/api/admin/admins/2`

**Headers:**
- `Authorization`: Bearer {{adminToken}}

---

## Test Scenarios

### Scenario 1: Complete Admin Workflow
1. Login as default admin
2. Create new admin
3. Get all admins (should see 2)
4. Update new admin
5. Delete new admin
6. Get all admins (should see 1)

### Scenario 2: Complete Therapist Workflow
1. Login as admin
2. Register therapist
3. Therapist logs in successfully (ACTIVE)
4. Admin suspends therapist
5. Therapist login fails (SUSPENDED)
6. Admin reactivates therapist
7. Therapist logs in successfully (ACTIVE)
8. Admin deletes therapist

### Scenario 3: RBAC Testing
1. Login as admin → Get admin token
2. Try to access /api/admin/therapists with admin token → Success ✓
3. Login as therapist → Get therapist token
4. Try to access /api/admin/therapists with therapist token → 403 Forbidden ✗
5. Try to access /api/therapist/** with therapist token → Success ✓

### Scenario 4: Status Validation
1. Register therapist (status: ACTIVE)
2. Therapist login → Success ✓
3. Admin sets status to INACTIVE
4. Therapist login → Fail (Account is INACTIVE) ✗
5. Admin sets status to SUSPENDED
6. Therapist login → Fail (Account is SUSPENDED) ✗
7. Admin sets status to ACTIVE
8. Therapist login → Success ✓

---

## Common Errors and Solutions

### 401 Unauthorized
- **Cause**: Missing or invalid token
- **Solution**: Login again and get fresh token

### 403 Forbidden
- **Cause**: Insufficient permissions (wrong role)
- **Solution**: Use correct role token (admin for /api/admin/**)

### 400 Bad Request
- **Cause**: Validation error (missing required fields)
- **Solution**: Check request body matches DTO requirements

### 500 Internal Server Error
- **Cause**: Email already exists or database error
- **Solution**: Use unique email or check database connection

---

## Import to Postman

1. Create new collection: "WellConnect API"
2. Add environment with baseUrl variable
3. Copy each request above
4. Set up test scripts to auto-save tokens
5. Run collection to test all endpoints

---

## cURL Examples

### Admin Login
```bash
curl -X POST http://localhost:8080/api/auth/admin/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@wellconnect.com","password":"admin123"}'
```

### Register Therapist
```bash
curl -X POST http://localhost:8080/api/admin/therapists/register \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "fullName":"Dr. Sarah Johnson",
    "email":"sarah.johnson@wellconnect.com",
    "specialisation":"CBT",
    "password":"Therapist123!"
  }'
```

### Therapist Login
```bash
curl -X POST http://localhost:8080/api/auth/therapist/login \
  -H "Content-Type: application/json" \
  -d '{"email":"sarah.johnson@wellconnect.com","password":"Therapist123!"}'
```
