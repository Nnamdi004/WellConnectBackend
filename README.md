# WellConnectBackend

# Wellconnect 

A mental health platform connecting patients with therapists — featuring real-time chat, mood tracking, community stories, and intelligent intake scoring.

**Live demo:** https://well-connect-frontend-mauve.vercel.app

**Frontend repository:** https://github.com/Nnamdi004/WellConnectFrontend.git

**API documentation:** https://wellconnectbackend.onrender.com/swagger-ui/index.html

**Project presentation**: https://www.overleaf.com/read/hrggqhfkqyng#cca31c

**Video Demo**: https://youtu.be/4iNEJFKcLDw

## Table of Contents
 
- [Overview](#-overview)
- [Tech Stack](#-tech-stack)
- [Architecture Overview](#-architecture-overview)
- [Project Structure](#-project-structure)
- [Getting Started](#-getting-started)
- [Environment Variables](#-environment-variables)
- [API Documentation](#-api-documentation)
- [Authentication & Security](#-authentication--security)
- [WebSocket / Real-time Chat](#-websocket--real-time-chat)
- [Running Tests](#-running-tests)
- [Deployment](#-deployment)
- [Contributing](#-contributing)
- [License](#-license)
 
---
 
## Overview
 
WellConnect is a RESTful backend platform built to bridge the gap between individuals seeking mental health support and licensed therapists. It provides:
 
- **Patient onboarding** via an intelligent intake questionnaire with severity scoring
  
- **Therapist discovery & appointment booking**

- **Real-time encrypted chat** between patients and therapists via WebSocket
  
- **Community features** — stories, comments, reactions, and tags
  
- **Mood tracking** and longitudinal mental health logging
  
- **Admin dashboard** for content moderation and user management
  
- **Notification system** for appointments, messages, and platform events
 
The platform enforces role-based access control across three user roles: `USER`, `THERAPIST`, and `ADMIN`.
 
---
 
## Tech Stack
 
| Layer | Technology | Purpose |
|---|---|---|
| Language | Java 21 | Core application language |
| Framework | Spring Boot 3.x | REST API, dependency injection, security |
| Database | PostgreSQL | Primary relational data store |
| Cache / Rate Limiting | Redis | Session caching, rate limit counters |
| Real-time | WebSocket (STOMP) | Bidirectional therapist-patient chat |
| Security | Spring Security + JWT | Authentication & authorization |
| API Docs | SpringDoc OpenAPI (Swagger) | Interactive API documentation |
| Build Tool | Maven (mvnw) | Dependency management & build |
| Containerization | Docker + Docker Compose | Local & production container setup |
| Deployment | Render.com | Cloud hosting via `render.yaml` |
 
---
 
## Architecture Overview
 
WellConnect follows a standard **layered architecture**:
 
```
HTTP Request / WebSocket
        │
        ▼
┌─────────────────────┐
│   Security Layer    │  JWT Auth Filter, Rate Limit Interceptor
└────────┬────────────┘
         │
         ▼
┌─────────────────────┐
│   Controller Layer  │  REST endpoints + WebSocket controllers
└────────┬────────────┘
         │
         ▼
┌─────────────────────┐
│   Service Layer     │  Business logic, validation, orchestration
└────────┬────────────┘
         │
         ▼
┌─────────────────────┐
│  Repository Layer   │  Spring Data JPA — database queries
└────────┬────────────┘
         │
         ▼
┌─────────────────────┐
│     PostgreSQL      │  Persistent data store
└─────────────────────┘
         +
┌─────────────────────┐
│       Redis         │  Rate limiting, caching
└─────────────────────┘
```
 
**Key cross-cutting concerns:**

- All chat messages are **encrypted at rest** via `EncryptionUtil`
  
- JWT tokens are validated on every request via `JwtAuthFilter`
  
- Rate limiting is enforced per-endpoint via the `@RateLimit` annotation and `RateLimitInterceptor`
  
- `GlobalExceptionHandler` provides consistent error responses across the API
 
---
 
## Project Structure
 
```
wellconnect/
├── .mvn/wrapper/
│   └── maven-wrapper.properties          # Maven wrapper configuration
│
├── src/
│   ├── main/
│   │   ├── java/com/alu/wellconnect/
│   │   │   ├── config/                   # Application configuration
│   │   │   │   ├── DataSeeder.java       # Seeds initial data on startup
│   │   │   │   ├── OpenApiConfig.java    # Swagger/OpenAPI setup
│   │   │   │   ├── RedisConfig.java      # Redis connection & caching config
│   │   │   │   ├── SecurityConfig.java   # Spring Security & JWT setup
│   │   │   │   ├── WebConfig.java        # CORS and MVC configuration
│   │   │   │   └── WebSocketConfig.java  # WebSocket broker configuration
│   │   │   │
│   │   │   ├── controller/               # REST API & WebSocket controllers
│   │   │   │   ├── AdminController.java
│   │   │   │   ├── AppointmentController.java
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── CategoryController.java
│   │   │   │   ├── ChatController.java
│   │   │   │   ├── ChatWebSocketController.java
│   │   │   │   ├── CommentController.java
│   │   │   │   ├── ContentReportController.java
│   │   │   │   ├── IntakeController.java
│   │   │   │   ├── MoodLogController.java
│   │   │   │   ├── NotificationController.java
│   │   │   │   ├── StoryController.java
│   │   │   │   ├── TagController.java
│   │   │   │   └── TherapistController.java
│   │   │   │
│   │   │   ├── dto/                      # Data Transfer Objects (request/response)
│   │   │   │   ├── AdminRegisterRequest.java
│   │   │   │   ├── AuthResponse.java
│   │   │   │   ├── CategoryRequest.java
│   │   │   │   ├── ChatMessagePayload.java
│   │   │   │   ├── CommentRequest.java
│   │   │   │   ├── CommentResponse.java
│   │   │   │   ├── CreateMoodRequest.java
│   │   │   │   ├── CreateReportRequest.java
│   │   │   │   ├── IntakeRequest.java
│   │   │   │   ├── IntakeResponse.java
│   │   │   │   ├── LoginRequest.java
│   │   │   │   ├── NotificationResponse.java
│   │   │   │   ├── RegisterRequest.java
│   │   │   │   ├── SessionCloseRequest.java
│   │   │   │   ├── SessionResponse.java
│   │   │   │   ├── SessionStartRequest.java
│   │   │   │   ├── StoryRequest.java
│   │   │   │   ├── StoryResponse.java
│   │   │   │   ├── TagRequest.java
│   │   │   │   ├── TherapistRegisterRequest.java
│   │   │   │   ├── TherapistResponse.java
│   │   │   │   ├── TherapistUpdateRequest.java
│   │   │   │   └── UpdateReportRequest.java
│   │   │   │
│   │   │   ├── entity/                   # JPA entities (database models)
│   │   │   │   ├── Appointment.java
│   │   │   │   ├── AppointmentStatus.java
│   │   │   │   ├── ChatMessage.java
│   │   │   │   ├── ChatSession.java
│   │   │   │   ├── ChatSessionStatus.java
│   │   │   │   ├── Comment.java
│   │   │   │   ├── ContentReport.java
│   │   │   │   ├── IntakeQuestionnaire.java
│   │   │   │   ├── MoodLog.java
│   │   │   │   ├── Notification.java
│   │   │   │   ├── NotificationType.java
│   │   │   │   ├── SenderRole.java
│   │   │   │   ├── Story.java
│   │   │   │   ├── StoryCategory.java
│   │   │   │   ├── StoryReaction.java
│   │   │   │   ├── Tag.java
│   │   │   │   ├── Therapist.java
│   │   │   │   └── User.java
│   │   │   │
│   │   │   ├── enums/                    # Shared enumerations
│   │   │   │   ├── SeverityLevel.java    # Intake severity levels
│   │   │   │   ├── StoryStatus.java      # DRAFT, PUBLISHED, FLAGGED, etc.
│   │   │   │   └── Visibility.java       # PUBLIC, PRIVATE, ANONYMOUS
│   │   │   │
│   │   │   ├── exception/                # Global error handling
│   │   │   │   └── GlobalExceptionHandler.java
│   │   │   │
│   │   │   ├── repository/               # Spring Data JPA repositories
│   │   │   │   ├── AppointmentRepository.java
│   │   │   │   ├── ChatMessageRepository.java
│   │   │   │   ├── ChatSessionRepository.java
│   │   │   │   ├── CommentRepository.java
│   │   │   │   ├── ContentReportRepository.java
│   │   │   │   ├── IntakeQuestionnaireRepository.java
│   │   │   │   ├── MoodLogRepository.java
│   │   │   │   ├── NotificationRepository.java
│   │   │   │   ├── StoryCategoryRepository.java
│   │   │   │   ├── StoryReactionRepository.java
│   │   │   │   ├── StoryRepository.java
│   │   │   │   ├── TagRepository.java
│   │   │   │   ├── TherapistRepository.java
│   │   │   │   └── UserRepository.java
│   │   │   │
│   │   │   ├── security/                 # JWT filter & rate limiting
│   │   │   │   ├── JwtAuthFilter.java    # Validates JWT on every request
│   │   │   │   ├── RateLimit.java        # Custom annotation for rate limiting
│   │   │   │   └── RateLimitInterceptor.java
│   │   │   │
│   │   │   ├── service/                  # Business logic layer
│   │   │   │   ├── AdminService.java
│   │   │   │   ├── AppointmentService.java
│   │   │   │   ├── AuthService.java
│   │   │   │   ├── CategoryService.java
│   │   │   │   ├── ChatSessionService.java
│   │   │   │   ├── CommentService.java
│   │   │   │   ├── ContentReportService.java
│   │   │   │   ├── IntakeService.java
│   │   │   │   ├── MoodLogService.java
│   │   │   │   ├── NotificationService.java
│   │   │   │   ├── RateLimitingService.java
│   │   │   │   ├── StoryService.java
│   │   │   │   ├── TagService.java
│   │   │   │   └── TherapistService.java
│   │   │   │
│   │   │   ├── util/                     # Utility/helper classes
│   │   │   │   ├── EncryptionUtil.java   # AES message encryption
│   │   │   │   └── JwtUtil.java          # JWT generation & validation
│   │   │   │
│   │   │   └── WellconnectApplication.java  # Spring Boot entry point
│   │   │
│   │   └── resources/
│   │       └── application.yaml          # App configuration (DB, Redis, JWT, etc.)
│   │
│   └── test/java/com/alu/wellconnect/
│       ├── controller/                   # Controller-level tests
│       │   ├── ApiValidationTest.java
│       │   ├── CommentControllerTest.java
│       │   ├── EndToEndPatientJourneyTest.java
│       │   ├── SecurityIntegrationTest.java
│       │   └── StoryControllerTest.java
│       ├── repository/                   # Repository / database tests
│       │   ├── DatabaseIntegrationTest.java
│       │   ├── DatabaseIntegrityTest.java
│       │   └── TherapistRepositoryTest.java
│       ├── service/                      # Service unit tests
│       │   ├── AppointmentServiceTest.java
│       │   ├── AuthSecurityTest.java
│       │   ├── AuthenticationCoreTest.java
│       │   ├── CommentServiceTest.java
│       │   ├── IntakeServiceTest.java
│       │   └── NotificationServiceTest.java
│       ├── PatientJourneyE2E.java         # Full end-to-end patient flow
│       └── WellconnectApplicationTests.java
│
├── .env.example                          # Environment variable template
├── docker-compose.yml                    # Local Docker services (DB, Redis)
├── Dockerfile                            # App container definition
├── render.yaml                           # Render.com deployment config
├── pom.xml                               # Maven dependencies & build config
└── README.md
```
 
---
 
## Getting Started
 
### Prerequisites
 
- [Java 21](https://openjdk.org/projects/jdk/21/)
- [Docker & Docker Compose](https://docs.docker.com/get-docker/)
- A running **Redis** instance (cloud-hosted, e.g. Redis Cloud or Upstash — Redis is **not** included in the local Docker Compose setup)
- Maven (or use the included `./mvnw` wrapper — no separate install needed)
 
### 1. Clone the repository
 
```bash
git clone https://github.com/your-org/wellconnect.git
cd wellconnect
```
 
### 2. Configure environment variables
 
```bash
cp .env.example .env
# Edit .env with your values (see Environment Variables section below)
```
 
### 3a. Run with Docker Compose (recommended)
 
This starts the Spring Boot app and a PostgreSQL database. Make sure your `.env` includes a reachable Redis host.
 
```bash
docker-compose up --build
```
 
The API will be available at `http://localhost:10000`.
 
### 3b. Run locally without Docker
 
Ensure PostgreSQL is running and your `.env` values are exported, then:
 
```bash
./mvnw spring-boot:run
```
 
Or build and run the JAR directly:
 
```bash
./mvnw clean package -DskipTests
java -jar target/WellConnect-backend-0.0.1-SNAPSHOT.jar
```
 
The app defaults to port **`8080`** locally (overridden to `10000` in Docker/Render via the `PORT` / `SERVER_PORT` environment variable).
 
> **Note:** Tests use an in-memory H2 database, so no PostgreSQL instance is required to run the test suite.
 
---
 
## Environment Variables
 
Copy `.env.example` to `.env` and fill in the values:
 
| Variable | Description | Example |
|---|---|---|
| `SPRING_DATASOURCE_URL` | PostgreSQL JDBC connection URL | `jdbc:postgresql://localhost:5432/wellconnect` |
| `SPRING_DATASOURCE_USERNAME` | Database username | `postgres` |
| `SPRING_DATASOURCE_PASSWORD` | Database password | `yourpassword` |
| `REDIS_HOST` | Redis host (remote recommended) | `redis-xyz.upstash.io` |
| `REDIS_PORT` | Redis port | `6379` |
| `REDIS_PASSWORD` | Redis password (leave blank if none) | `yourredispassword` |
| `JWT_SECRET` | Secret key for signing JWTs (min. 32 chars) | `your-256-bit-secret-key` |
| `JWT_EXPIRATION` | JWT token expiry in milliseconds | `86400000` (24 hours) |
| `ENCRYPTION_KEY` | AES-128 key for chat encryption (**must be exactly 16 characters**) | `1234567890123456` |
| `SERVER_PORT` | Port the app listens on | `8080` (local) / `10000` (Docker/Render) |
| `ADMIN_NAME` | Default admin display name | `Admin` |
| `ADMIN_EMAIL` | Default admin account email | `admin@wellconnect.com` |
| `ADMIN_PASSWORD` | Default admin account password | `admin123` |
| `SPRING_PROFILES_ACTIVE` | Active Spring profile | `prod` |
 
> **Never commit your `.env` file.** It is listed in `.gitignore`.
 
---
 
## API Documentation
 
Interactive API documentation is available via Swagger UI:
 
**Live:** [https://wellconnectbackend.onrender.com/swagger-ui/index.html](https://wellconnectbackend.onrender.com/swagger-ui/index.html)
 
**Local:** [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
 
The raw OpenAPI spec is available at `/api-docs`.
 
### Endpoint Groups
 
| Group | Base Path | Description |
|---|---|---|
| Auth | `/api/auth` | Register, login, token management |
| Admin | `/api/admin` | User management, platform oversight |
| Therapists | `/api/therapists` | Therapist profiles and availability |
| Appointments | `/api/appointments` | Book, update, and cancel appointments |
| Chat | `/api/chat` | Chat session management (start/close via REST) |
| Intake | `/api/intake` | Patient intake questionnaire submission |
| Mood Logs | `/api/moods` | Log and retrieve mood entries |
| Stories | `/api/stories` | Community story feed and reactions |
| Comments | `/api/comments` | Comment on stories |
| Tags & Categories | `/api/tags`, `/api/categories` | Content organisation |
| Content Reports | `/api/reports` | Flag and moderate content |
| Notifications | `/api/notifications` | In-app notifications |
 
A full Postman collection is included in the repository: `WellConnect_API_v1.postman_collection.json`
 
---
 
## Authentication & Security
 
### JWT Authentication
 
All protected endpoints require a `Bearer` token in the `Authorization` header:
 
```
Authorization: Bearer <your-jwt-token>
```
 
Tokens are issued on login (`POST /api/auth/login`) and must be included in every subsequent request. The `JwtAuthFilter` validates the token on each request before it reaches the controller.
 
### Role-Based Access Control
 
Three roles govern access across the platform:
 
| Role | Description |
|---|---|
| `PATIENT` | Standard user — can book appointments, chat, post stories, and log moods |
| `THERAPIST` | Can manage sessions, view patient intake data, and conduct chats |
| `ADMIN` | Full platform access — manages users, content reports, and therapists |
 
Endpoints are secured using Spring Security method-level annotations (`@PreAuthorize`).
 
### Rate Limiting
 
Sensitive endpoints are annotated with `@RateLimit`. The `RateLimitInterceptor` uses **Bucket4j** backed by Redis to enforce per-IP/user request quotas. Exceeding the limit returns `429 Too Many Requests`.
 
Examples from the codebase:
- `POST /api/auth/register` — 3 registrations per hour per IP
- `POST /api/auth/login` — 5 login attempts per minute per IP
 
### Message Encryption
 
All chat messages are encrypted at rest using **AES-128** (`EncryptionUtil`) before being persisted to the database, and decrypted transparently on retrieval. The encryption key must be exactly 16 characters, set via the `ENCRYPTION_KEY` environment variable.
 
---
 
## WebSocket / Real-time Chat
 
WellConnect uses **STOMP over SockJS** for real-time messaging between patients and therapists.
 
### Connecting
 
```javascript
const socket = new SockJS('http://localhost:8080/ws-chat');
const stompClient = Stomp.over(socket);
 
stompClient.connect({ Authorization: 'Bearer <token>' }, () => {
  // Connected successfully
});
```
 
### Subscribing to a Chat Session
 
```javascript
stompClient.subscribe(`/topic/session/{sessionId}`, (message) => {
  const payload = JSON.parse(message.body);
  console.log(payload);
});
```
 
### Sending a Message
 
```javascript
stompClient.send('/app/chat.send', {}, JSON.stringify({
  sessionId: 'uuid',
  content: 'Hello!',
}));
```
 
### Chat Session Lifecycle
 
Sessions are created and managed via the REST API before messages are exchanged over WebSocket:
 
```
Patient requests session  →  POST /api/chat/{appointmentId}/start  →  Session (ACTIVE)
Real-time messages        →  WebSocket /ws-chat  ↔  /topic/session/{id}
Either party closes       →  PUT  /api/chat/{sessionId}/close       →  Session (CLOSED)
```
 
A confirmed `Appointment` must exist before a chat session can be started.
 
---
 
## Running Tests
 
Tests use an **in-memory H2 database**, so no external PostgreSQL or Redis instance is required.
 
### Run all tests
 
```bash
./mvnw test
```
 
### Test coverage
 
| Layer | Test Classes |
|---|---|
| Controllers | `ApiValidationTest`, `CommentControllerTest`, `StoryControllerTest`, `SecurityIntegrationTest` |
| Services | `AuthenticationCoreTest`, `AppointmentServiceTest`, `CommentServiceTest`, `IntakeServiceTest`, `NotificationServiceTest`, `AuthSecurityTest` |
| Repositories | `DatabaseIntegrationTest`, `DatabaseIntegrityTest`, `TherapistRepositoryTest` |
| End-to-End | `EndToEndPatientJourneyTest`, `PatientJourneyE2E` |
 
---
 
## Deployment
 
### Docker
 
Build and run the container manually:
 
```bash
docker build -t wellconnect-api .
docker run -p 10000:10000 --env-file .env wellconnect-api
```
 
The container exposes port **`10000`** and sets `SPRING_PROFILES_ACTIVE=prod` by default.
 
### Docker Compose (local full stack)
 
```bash
docker-compose up --build
```
 
This starts the Spring Boot app (`port 10000`) and a PostgreSQL database. Redis must be provided externally via environment variables — it is not included in the Compose setup.
 
### Render.com
 
The project includes a `render.yaml` for deployment to [Render](https://render.com).
 
1. Push the repository to GitHub.
2. Connect the repo in the Render dashboard.
3. Set all required environment variables in the Render service settings (see [Environment Variables](#environment-variables)).
4. Render will build using the `Dockerfile` and deploy automatically on every push to `main`.
 
**Live API:** [https://wellconnectbackend.onrender.com](https://wellconnectbackend.onrender.com)
 
> Render free-tier services spin down after inactivity. The first request after a period of inactivity may take 30–60 seconds to respond while the service cold-starts.
 
---
 
## Contributions

**Backend** 

Michelle Anyika 

Umutoni Nada

Bendou Janna Vitalina Soeur 

**Frontend**

Chibueze Onugha

Chely Kelvin Sheja Indamutsa

Frontend repo: https://github.com/Nnamdi004/WellConnectFrontend.git
---
 
*Built with ❤️ for mental health accessibility.*
