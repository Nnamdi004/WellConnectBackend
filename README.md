# WellConnectBackend

# WellConnect Frontend

![Next.js](https://img.shields.io/badge/Next.js-16-black?logo=next.js)
![React](https://img.shields.io/badge/React-19-61dafb?logo=react)
![TypeScript](https://img.shields.io/badge/TypeScript-5.9-3178c6?logo=typescript)
![Tailwind CSS](https://img.shields.io/badge/Tailwind_CSS-4-06b6d4?logo=tailwindcss)
![License](https://img.shields.io/badge/License-ISC-green)

WellConnect Backend is a Spring Boot–based API that powers the WellConnect mental health platform. It handles authentication, anonymous story sharing, therapist booking, real-time chat sessions, mood tracking, and clinical intake assessment (PHQ-9 and GAD-7).

---

## 🌐 API Base URLs

- **Production:**
https://wellconnectbackend.onrender.com
---

## 📚 API Documentation

OpenAPI specification available at:

/api-docs

Description:
Mental health support platform API for anonymous story sharing and emotional healing

---

## 🔐 Authentication & Authorization

### Roles
- USER
- THERAPIST
- ADMIN

### Endpoints
- `POST /api/auth/register`
- `POST /api/auth/login`
- `POST /api/auth/admin/login`
- `POST /api/auth/therapist/login`

### Mechanism
- JWT-based authentication using Spring Security
- Tokens returned in `AuthResponse`
- Included in requests as:

Authorization: Bearer <token>

### Notes
- Therapist login includes status validation
- Admin endpoints are under `/api/admin/*`

---

## 🧩 Core Modules

### 👤 Authentication
Handles user, therapist, and admin authentication.

---

### 📖 Stories
- `GET /api/stories` — Get public story feed
- `POST /api/stories` — Create story
- `GET /api/stories/{id}` — Get story by ID
- `POST /api/stories/{id}/like` — Like story
- `DELETE /api/stories/{id}/like` — Unlike story

---

### 💬 Comments
- `GET /api/stories/{storyId}/comments`
- `POST /api/stories/{storyId}/comments`

---

### 😊 Mood Tracking
- `POST /api/moods` — Log mood
- `GET /api/moods` — Retrieve mood history

---

### 📊 Intake Assessment
- `POST /api/intake` — Submit questionnaire
- `GET /api/intake/me` — Get results

Supports:
- PHQ-9 (depression screening)
- GAD-7 (anxiety screening)

---

### 📅 Appointments
- `POST /api/appointments` — Book appointment
- `GET /api/therapists/appointments` — Therapist/Admin view

---

### 🧑‍⚕️ Therapist Management
- `POST /api/admin/therapists/register`
- `GET /api/admin/therapists`
- `GET /api/admin/therapists/{id}`
- `PUT /api/admin/therapists/{id}`
- `DELETE /api/admin/therapists/{id}`
- `GET /api/therapists/{id}/availability`

---

### 🛠️ Admin Management
- `POST /api/admin/admins`
- `GET /api/admin/admins`
- `PUT /api/admin/admins/{id}`
- `DELETE /api/admin/admins/{id}`

---

### 🏷️ Categories & Tags

#### Categories
- `POST /api/admin/categories`
- `GET /api/categories`

#### Tags
- `POST /api/admin/tags`
- `GET /api/tags`

---

### 🚨 Content Moderation
- `POST /api/reports`
- `GET /api/admin/reports`
- `PUT /api/admin/reports/{reportId}`
- `PUT /api/admin/reports/{reportId}/resolve`

---

### 🔔 Notifications
- `GET /api/notifications/me`
- `PUT /api/notifications/{id}/read`

---

### 💬 Chat System

#### REST
- `POST /api/chat/{appointmentId}/start`
- `PUT /api/chat/{sessionId}/close`

#### WebSocket
Handles real-time messaging between users and therapists.

---

## ⚙️ Tech Stack

| Layer | Technology |
|------|-----------|
| Language | Java |
| Framework | Spring Boot |
| Build Tool | Maven |
| ORM | Spring Data JPA |
| Security | Spring Security + JWT |
| Real-time | WebSocket |
| API Docs | OpenAPI 3 |
| Deployment | Docker · Render |

---

## 🏗️ Architecture

Controller → Service → Repository → Database

### Project Structure

src/main/java/com/alu/wellconnect
- config (Security, WebSocket, OpenAPI)
- controller (API endpoints)
- dto (Request/Response objects)
- entity (Database models)
- repository (Data access)
- service (Business logic)
- security (JWT filter)
- util (Utilities)

---

## ⚙️ Getting Started

### Prerequisites
- Java 17+
- Maven
- Docker (optional)

---

### Installation

git clone https://github.com/Nnamdi004/WellConnectBackend.git
cd WellConnectBackend
mvn clean install

---

## 🐳 Docker

docker-compose up --build

---

## 📁 Key Components

- `SecurityConfig.java` — Security configuration
- `JwtAuthFilter.java` — Request authentication
- `JwtUtil.java` — Token generation
- `EncryptionUtil.java` — Encryption utilities
- `DataSeeder.java` — Initial data setup

---

## ⚠️ Scope

### This backend:
- Provides REST APIs
- Manages authentication and roles
- Handles business logic
- Supports real-time chat
- Implements intake scoring

### It does NOT:
- Include frontend UI
- Handle Google OAuth directly
- Use microservices architecture

---

## ❤️ Purpose

Built to support accessible, secure, and stigma-free mental health care through technology.


## 👥 Team

Michelle, Janna and Umutoni whom are part of the WellConnect platform backend system.

