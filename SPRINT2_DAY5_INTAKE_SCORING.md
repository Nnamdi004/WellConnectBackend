# Sprint 2 - Day 5: Clinical Intake & Severity Scoring

## Overview
This feature implements a clinical intake questionnaire system that evaluates mental health severity using standardized PHQ-9 (depression) and GAD-7 (anxiety) scoring instruments.

---

## Features Implemented

### 1. Database Entity
- **Table**: `intake_questionnaire`
- **Unique Constraint**: One active baseline intake per user
- **Columns**:
  - `intake_id` (Primary Key)
  - `user_id` (Foreign Key → USERS, UNIQUE)
  - `phq9_score` (Integer, 0-27)
  - `gad7_score` (Integer, 0-21)
  - `severity_level` (ENUM: MINIMAL, MILD, MODERATE, SEVERE)
  - `submitted_at` (Timestamp)

### 2. Scoring Engine
Automated severity calculation based on clinical thresholds:

| Condition | Severity Level |
|-----------|---------------|
| Either score ≥ 15 | **SEVERE** |
| Either score ≥ 10 | **MODERATE** |
| Either score ≥ 5 | **MILD** |
| Both scores < 5 | **MINIMAL** |

### 3. API Endpoints

#### POST /api/intake
Submit intake questionnaire scores.

**Request Body:**
```json
{
  "phq9Score": 12,
  "gad7Score": 8
}
```

**Response:**
```json
{
  "intakeId": 1,
  "userId": 5,
  "phq9Score": 12,
  "gad7Score": 8,
  "severityLevel": "MODERATE",
  "submittedAt": "2026-03-08T10:30:00"
}
```

**Validation:**
- PHQ-9 score: 0-27 (9 questions × 0-3 points)
- GAD-7 score: 0-21 (7 questions × 0-3 points)
- User must be authenticated
- User cannot submit multiple intakes (unique constraint)

#### GET /api/intake/me
Retrieve logged-in user's intake results.

**Response:**
```json
{
  "intakeId": 1,
  "userId": 5,
  "phq9Score": 12,
  "gad7Score": 8,
  "severityLevel": "MODERATE",
  "submittedAt": "2026-03-08T10:30:00"
}
```

---

## Architecture

### Entity Layer
```
IntakeQuestionnaire
├── intakeId (PK)
├── userId (FK, UNIQUE)
├── phq9Score
├── gad7Score
├── severityLevel (ENUM)
└── submittedAt
```

### Service Layer
**IntakeService** contains:
- `submitIntake()`: Validates user, checks for existing intake, calculates severity, saves record
- `getMyIntake()`: Retrieves user's intake results
- `calculateSeverityLevel()`: Private scoring engine implementing clinical thresholds

### Controller Layer
**IntakeController** provides:
- JWT-secured endpoints
- Automatic user extraction from authentication token
- Input validation
- Swagger documentation

---

## Clinical Scoring Standards

### PHQ-9 (Patient Health Questionnaire-9)
Measures depression severity over the past 2 weeks.

**Score Range**: 0-27
- 0-4: Minimal depression
- 5-9: Mild depression
- 10-14: Moderate depression
- 15-19: Moderately severe depression
- 20-27: Severe depression

### GAD-7 (Generalized Anxiety Disorder-7)
Measures anxiety severity over the past 2 weeks.

**Score Range**: 0-21
- 0-4: Minimal anxiety
- 5-9: Mild anxiety
- 10-14: Moderate anxiety
- 15-21: Severe anxiety

### Combined Severity Logic
The system uses the **higher** of the two scores to determine overall severity level, ensuring that significant symptoms in either domain are properly flagged.

---

## Security Features

1. **JWT Authentication Required**: All endpoints require valid JWT token
2. **User Isolation**: Users can only access their own intake data
3. **Unique Constraint**: Prevents duplicate baseline intakes
4. **Input Validation**: Score ranges validated against clinical standards

---

## Testing Guide

### Test Case 1: Submit Minimal Severity Intake
```bash
curl -X POST http://localhost:8081/api/intake \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "phq9Score": 2,
    "gad7Score": 3
  }'
```
**Expected**: `severityLevel: "MINIMAL"`

### Test Case 2: Submit Mild Severity Intake
```bash
curl -X POST http://localhost:8081/api/intake \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "phq9Score": 7,
    "gad7Score": 5
  }'
```
**Expected**: `severityLevel: "MILD"`

### Test Case 3: Submit Moderate Severity Intake
```bash
curl -X POST http://localhost:8081/api/intake \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "phq9Score": 12,
    "gad7Score": 8
  }'
```
**Expected**: `severityLevel: "MODERATE"`

### Test Case 4: Submit Severe Severity Intake
```bash
curl -X POST http://localhost:8081/api/intake \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "phq9Score": 18,
    "gad7Score": 16
  }'
```
**Expected**: `severityLevel: "SEVERE"`

### Test Case 5: Retrieve My Intake
```bash
curl -X GET http://localhost:8081/api/intake/me \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Test Case 6: Duplicate Submission (Should Fail)
```bash
# Submit second intake for same user
curl -X POST http://localhost:8081/api/intake \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "phq9Score": 5,
    "gad7Score": 5
  }'
```
**Expected**: Error message about existing intake

### Test Case 7: Invalid Score Range
```bash
curl -X POST http://localhost:8081/api/intake \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "phq9Score": 30,
    "gad7Score": 25
  }'
```
**Expected**: Validation error

---

## Error Handling

| Error | HTTP Status | Message |
|-------|-------------|---------|
| User not found | 404 | "User not found" |
| Duplicate intake | 400 | "User already has an active intake questionnaire" |
| No intake found | 404 | "No intake questionnaire found for this user" |
| Invalid score range | 400 | Validation error messages |
| Unauthorized | 401 | "Unauthorized" |

---

## Database Schema

```sql
CREATE TABLE intake_questionnaire (
    intake_id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    phq9_score INT NOT NULL CHECK (phq9_score BETWEEN 0 AND 27),
    gad7_score INT NOT NULL CHECK (gad7_score BETWEEN 0 AND 21),
    severity_level VARCHAR(20) NOT NULL,
    submitted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE INDEX idx_intake_user_id ON intake_questionnaire(user_id);
CREATE INDEX idx_intake_severity ON intake_questionnaire(severity_level);
```

---

## Files Created

1. **Entity**: `IntakeQuestionnaire.java`
2. **Enum**: `SeverityLevel.java`
3. **DTOs**: 
   - `IntakeRequest.java`
   - `IntakeResponse.java`
4. **Repository**: `IntakeQuestionnaireRepository.java`
5. **Service**: `IntakeService.java`
6. **Controller**: `IntakeController.java`

---

## Integration with Existing System

### Authentication Flow
1. User logs in → Receives JWT token
2. User submits intake → Token validated, email extracted
3. System finds user by email → Validates no existing intake
4. Scores calculated → Severity determined → Record saved

### Future Enhancements
- Allow intake updates/retakes after a time period
- Track intake history over time
- Generate clinical reports
- Therapist dashboard to view patient severity levels
- Automated alerts for severe cases
- Integration with treatment recommendations

---

## Sprint 2 - Day 5 Checklist

- [x] Create `SeverityLevel` enum
- [x] Create `IntakeQuestionnaire` entity with unique user constraint
- [x] Implement scoring engine with clinical thresholds
- [x] Create `IntakeRequest` and `IntakeResponse` DTOs
- [x] Create `IntakeQuestionnaireRepository`
- [x] Implement `IntakeService` with business logic
- [x] Create `IntakeController` with secure endpoints
- [x] Add input validation (0-27 for PHQ-9, 0-21 for GAD-7)
- [x] Implement JWT-based user extraction
- [x] Add Swagger documentation
- [x] Prevent duplicate baseline intakes

**Status**: ✅ Complete

---

## Clinical References

- **PHQ-9**: Kroenke K, Spitzer RL, Williams JB. The PHQ-9: validity of a brief depression severity measure. J Gen Intern Med. 2001;16(9):606-613.
- **GAD-7**: Spitzer RL, Kroenke K, Williams JB, Löwe B. A brief measure for assessing generalized anxiety disorder: the GAD-7. Arch Intern Med. 2006;166(10):1092-1097.

---

**Developed by**: Michelle  
**Sprint**: 2 - Day 5  
**Date**: March 8, 2026
