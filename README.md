# MyKare Healthcare Appointment Platform

A simplified production-style healthcare appointment platform built for the backend engineer assignment.

The system contains:

- Spring Boot backend API
- PostgreSQL database
- Kafka event-driven communication
- Python notification worker
- React frontend
- Docker Compose setup

## Features

- User registration and login
- JWT-based authentication
- Fetch active doctors
- Fetch available appointment slots
- Book appointment
- Prevent duplicate active booking for the same slot
- Cancel appointment
- View appointment history
- View appointment lifecycle logs
- Publish appointment events to Kafka
- Python worker consumes Kafka events and updates processing status
- Swagger/OpenAPI documentation
- Dockerized full-stack setup

## Tech Stack

| Layer | Technology |
|---|---|
| Backend | Java 17, Spring Boot 3 |
| Auth | JWT, Spring Security, BCrypt |
| Database | PostgreSQL |
| Migration | Flyway |
| Messaging | Kafka |
| Worker | Python |
| Frontend | React, Vite |
| Containerization | Docker Compose |
| API Docs | Swagger/OpenAPI |

## Architecture

```text
React Frontend
    |
    | HTTP + JWT
    v
Spring Boot Backend
    |
    | PostgreSQL writes
    v
PostgreSQL
    ^
    | status/log updates
    |
Python Worker
    ^
    | Kafka events
    |
Kafka
```

## Services

| Service | Local URL |
|---|---|
| Frontend | `http://localhost:5173` |
| Backend API | `http://localhost:8080` |
| Swagger UI | `http://localhost:8080/swagger-ui.html` |
| PostgreSQL | `localhost:5433` |
| Kafka | `localhost:9092` |

## Run with Docker Compose

From project root:

```bash
docker compose up --build
```

Open frontend:

```text
http://localhost:5173
```

Open Swagger:

```text
http://localhost:8080/swagger-ui.html
```

Stop services:

```bash
docker compose down
```

Reset local database:

```bash
docker compose down -v
```

## Run Locally Without Full Docker

Start infrastructure:

```bash
docker compose up -d postgres zookeeper kafka
```

Run backend:

```bash
cd backend
POSTGRES_PORT=5433 mvn spring-boot:run
```

Run worker:

```bash
cd worker
python3 -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt
POSTGRES_PORT=5433 python -m app.main
```

Run frontend:

```bash
cd frontend
npm install
npm run dev
```

## Environment Variables

Example values:

```env
POSTGRES_DB=mykare_appointments
POSTGRES_USER=mykare
POSTGRES_PASSWORD=mykare_password
POSTGRES_PORT=5433

KAFKA_BROKER=localhost:9092
APPOINTMENT_EVENTS_TOPIC=appointment-events

JWT_SECRET=ARj4ptWyFeD26g9nQ8fjeTjLRKjjTcRxzstEfgC6cy8
JWT_EXPIRATION_MINUTES=1440
```

## Main API Endpoints

| Method | Endpoint | Auth | Purpose |
|---|---|---|---|
| `GET` | `/api/health` | No | Backend health check |
| `POST` | `/api/auth/register` | No | Register user |
| `POST` | `/api/auth/login` | No | Login user |
| `GET` | `/api/doctors` | Yes | Fetch doctors |
| `GET` | `/api/slots/available` | Yes | Fetch available slots |
| `POST` | `/api/appointments` | Yes | Book appointment |
| `PATCH` | `/api/appointments/{id}/cancel` | Yes | Cancel appointment |
| `GET` | `/api/appointments/my` | Yes | Fetch user appointments |
| `GET` | `/api/appointments/{id}/logs` | Yes | Fetch appointment logs |

## Authentication

After register/login, the backend returns a JWT token.

Use it for protected APIs:

```http
Authorization: Bearer <token>
```

## Event Flow

```text
User books appointment
    ↓
Spring Boot saves appointment with PENDING processing status
    ↓
Spring Boot publishes APPOINTMENT_BOOKED event to Kafka
    ↓
Python worker consumes event
    ↓
Worker marks appointment PROCESSING
    ↓
Worker simulates notification
    ↓
Worker marks appointment NOTIFICATION_SENT
    ↓
Worker inserts appointment logs
```

## Duplicate Booking Prevention

The database has a unique partial index:

```sql
CREATE UNIQUE INDEX uk_active_slot_booking
ON appointments(slot_id)
WHERE status = 'BOOKED';
```

This guarantees only one active booked appointment can exist for a slot, even if two users try to book at the same time.

## Demo Flow

1. Open frontend at `http://localhost:5173`.
2. Register or login.
3. Fetch available slots.
4. Book a slot.
5. See appointment in history.
6. Python worker processes Kafka event.
7. Refresh appointments/logs.
8. See `NOTIFICATION_SENT`.
9. Cancel appointment.
10. See cancellation logs.

## Useful Commands

Check running containers:

```bash
docker compose ps
```

Check backend logs:

```bash
docker compose logs backend
```

Check worker logs:

```bash
docker compose logs worker
```

Check Kafka topic:

```bash
docker exec -it mykare-kafka kafka-topics \
  --bootstrap-server localhost:9092 \
  --list
```

Consume appointment events:

```bash
docker exec -it mykare-kafka kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic appointment-events \
  --from-beginning
```

## Final Demo Links

| Item | Link |
|---|---|
| Frontend | `http://localhost:5173` |
| Swagger UI | `http://localhost:8080/swagger-ui.html` |
| Health Check | `http://localhost:8080/api/health` |
| Curl Flow | `docs/curl-flow.md` |
| Database Schema | `docs/schema.md` |
| Architecture | `docs/architecture.md` |
| Demo Checklist | `docs/demo-checklist.md` |

## Submission Notes

The repository includes:

- Backend source code
- Frontend source code
- Python worker source code
- Docker Compose setup
- Flyway database migration
- Swagger API documentation
- Database schema documentation
- Demo checklist
- Curl demo flow

For the demo video, start with:

```bash
docker compose up --build
```

Then open:

```text
http://localhost:5173
```