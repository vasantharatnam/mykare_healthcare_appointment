# MyKare Healthcare Appointment Platform

This repository contains a simplified production-style healthcare appointment platform for the backend engineer assignment.

## Services

| Service | Path | Responsibility |
|---|---|---|
| Spring Boot API | `backend/` | Authentication, appointments, slots, database writes, event publishing |
| Python Worker | `worker/` | Appointment event consumption, notification simulation, processing status updates |
| React UI | `frontend/` | Registration, login, slot browsing, booking, cancellation, status display |
| Documentation | `docs/` | API notes, schema, setup guide, demo checklist |

## Planned Local Stack

- Java 17 and Spring Boot 3
- PostgreSQL
- Kafka
- Python worker
- Vite React frontend
- Docker Compose

## Assignment Scope

The final application will support:

- User registration and login with JWT authentication
- Available slot discovery
- Appointment booking with duplicate booking prevention
- Appointment cancellation
- User appointment history
- Appointment logs and processing status
- Spring Boot to Python event flow through Kafka
- Swagger API documentation
- Dockerized local setup

## Docker Compose Setup

Run the full system:

```bash
docker compose up --build
```

Services:

| Service | URL |
|---|---|
| Frontend | `http://localhost:5173` |
| Backend | `http://localhost:8080` |
| Swagger | `http://localhost:8080/swagger-ui.html` |
| PostgreSQL | `localhost:5433` |
| Kafka | `localhost:9092` |

Stop services:

```bash
docker compose down
```

Reset database volume:

```bash
docker compose down -v
```