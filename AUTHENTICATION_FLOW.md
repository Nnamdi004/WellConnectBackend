# Authentication & Authorization Flow

## 1. Application Startup Flow
```
Application Start
    ↓
DataSeeder.run()
    ↓
Check if admin@wellconnect.com exists
    ↓
    NO → Create default admin
          - username: "admin"
          - email: "admin@wellconnect.com"
          - password: BCrypt("admin123")
          - role: ADMIN
          - status: ACTIVE
    ↓
    YES → Skip (admin already exists)
    ↓
Application Ready
```

## 2. Admin Login Flow
```
POST /api/auth/admin/login
    ↓
AdminService.loginAdmin()
    ↓
Find user by email
    ↓
Check if role == ADMIN
    ↓
Verify password (BCrypt)
    ↓
Generate JWT token
    - Subject: email
    - Claim: role = "ADMIN"
    ↓
Return AuthResponse
    - token
    - email
    - role
```

## 3. Admin Creates Therapist Flow
```
POST /api/admin/therapists/register
    ↓
@PreAuthorize("hasRole('ADMIN')") ← JWT Filter validates ADMIN role
    ↓
TherapistService.registerTherapist()
    ↓
Check if email exists
    ↓
Create Therapist entity
    - fullName: from request
    - email: from request
    - specialisation: from request
    - passwordHash: BCrypt(plain password)
    - status: ACTIVE (hardcoded)
    ↓
Save to database
    ↓
Return TherapistResponse
```

## 4. Therapist Login Flow (with Status Validation)
```
POST /api/auth/therapist/login
    ↓
TherapistService.loginTherapist()
    ↓
Find therapist by email
    ↓
Verify password (BCrypt)
    ↓
CHECK STATUS ← KEY VALIDATION
    ↓
    ACTIVE → Continue
    INACTIVE → Throw "Account is INACTIVE"
    SUSPENDED → Throw "Account is SUSPENDED"
    ↓
Generate JWT token
    - Subject: email
    - Claim: role = "THERAPIST"
    ↓
Return AuthResponse
    - token
    - email
    - role
```

## 5. JWT Authentication Flow
```
Request with Authorization: Bearer {token}
    ↓
JwtAuthFilter.doFilterInternal()
    ↓
Extract token from header
    ↓
Validate token signature
    ↓
Extract claims:
    - email (subject)
    - role (claim)
    ↓
Create Authentication object
    - Principal: email
    - Authority: "ROLE_" + role
    ↓
Set in SecurityContext
    ↓
Continue to controller
```

## 6. RBAC Authorization Flow
```
Request to /api/admin/therapists
    ↓
SecurityFilterChain checks URL pattern
    - /api/admin/** → requires ROLE_ADMIN
    ↓
@PreAuthorize("hasRole('ADMIN')") on method
    ↓
Check SecurityContext authentication
    ↓
    Has ROLE_ADMIN → Allow
    No ROLE_ADMIN → 403 Forbidden
    ↓
Execute controller method
```

## 7. Admin CRUD on Therapist Flow
```
Admin wants to suspend a therapist
    ↓
PUT /api/admin/therapists/{id}
Authorization: Bearer {admin_token}
Body: { "status": "SUSPENDED" }
    ↓
RBAC checks ADMIN role ✓
    ↓
TherapistService.updateTherapist()
    ↓
Find therapist by ID
    ↓
Update status to SUSPENDED
    ↓
Save to database
    ↓
Return updated TherapistResponse
    ↓
Therapist tries to login
    ↓
Status check fails → "Account is SUSPENDED"
```

## Security Layers

### Layer 1: URL-based Security (SecurityFilterChain)
```java
.requestMatchers("/api/admin/**").hasRole("ADMIN")
.requestMatchers("/api/therapist/**").hasRole("THERAPIST")
```

### Layer 2: Method-based Security (@PreAuthorize)
```java
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<TherapistResponse> registerTherapist(...)
```

### Layer 3: Business Logic Validation
```java
if (therapist.getStatus() != Therapist.Status.ACTIVE) {
    throw new RuntimeException("Account is " + therapist.getStatus().name());
}
```

## Role Hierarchy
```
ADMIN
  ├─ Can manage admins (CRUD)
  ├─ Can manage therapists (CRUD)
  ├─ Can access /api/admin/**
  └─ Full system access

THERAPIST
  ├─ Can login (if ACTIVE)
  ├─ Can access /api/therapist/**
  └─ Limited to own resources

USER
  ├─ Can login
  ├─ Can access /api/user/**
  └─ Basic user features
```

## Status States for Therapist

```
ACTIVE
  └─ Can login ✓
  └─ Can access system ✓

INACTIVE
  └─ Cannot login ✗
  └─ Account disabled

SUSPENDED
  └─ Cannot login ✗
  └─ Temporarily blocked
```

## Password Security Flow
```
Plain Password Input
    ↓
BCryptPasswordEncoder.encode()
    ↓
Hashed Password (60 chars)
    ↓
Store in password_hash column
    ↓
Never store plain text ✓

Login Verification:
Plain Password Input
    ↓
BCryptPasswordEncoder.matches(plain, hashed)
    ↓
Boolean result
```
