# API Documentation Notes

Swagger/OpenAPI is exposed by the Spring Boot service.

## Swagger URL

```text
http://localhost:8080/swagger-ui.html
```

## Health APIs

| Method | Path | Auth Required | Purpose |
|---|---|---|---|
| `GET` | `/api/health` | No | Application-level health smoke check |
| `GET` | `/actuator/health` | No | Spring Boot Actuator health check |

## Auth APIs

| Method | Path | Auth Required | Purpose |
|---|---|---|---|
| `POST` | `/api/auth/register` | No | Register a new user and receive JWT |
| `POST` | `/api/auth/login` | No | Login existing user and receive JWT |

## Register Request

```json
{
  "fullName": "Ratan Kumar",
  "email": "ratan@example.com",
  "password": "password123"
}
```

## Login Request

```json
{
  "email": "ratan@example.com",
  "password": "password123"
}
```

## Auth Response

```json
{
  "userId": 1,
  "fullName": "Ratan Kumar",
  "email": "ratan@example.com",
  "role": "USER",
  "token": "jwt-token-here",
  "message": "Login successful"
}
```

## Appointment APIs

| Method | Path | Auth Required | Purpose |
|---|---|---|---|
| `POST` | `/api/appointments` | Yes | Book an appointment |

### Book Appointment Request

```json
{
  "slotId": 1
}
```

### Book Appointment Response

```json
{
  "appointmentId": 1,
  "slotId": 1,
  "doctorId": 1,
  "doctorName": "Dr. Ananya Rao",
  "specialization": "General Medicine",
  "slotStart": "2026-07-01T09:00:00+05:30",
  "slotEnd": "2026-07-01T09:30:00+05:30",
  "status": "BOOKED",
  "processingStatus": "PENDING",
  "createdAt": "2026-06-21T10:00:00+05:30"
}
```

### Book Appointment Curl

```bash
curl -X POST http://localhost:8080/api/appointments \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{"slotId":1}'
```

## Authentication

Protected APIs require the JWT token in the `Authorization` header.

```http
Authorization: Bearer <token>
```

## Test Current APIs

Health:

```bash
curl http://localhost:8080/api/health
```

Register:

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"fullName":"Ratan Kumar","email":"ratan@example.com","password":"password123"}'
```

Login:

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"ratan@example.com","password":"password123"}'
```

## Error Response Format

```json
{
  "timestamp": "2026-06-20T22:00:00+05:30",
  "status": 400,
  "error": "Bad Request",
  "messages": [
    "email: Email must be valid"
  ]
}
```

## Appointment APIs

| Method | Path | Auth Required | Purpose |
|---|---|---|---|
| `POST` | `/api/appointments` | Yes | Book an appointment |
| `PATCH` | `/api/appointments/{id}/cancel` | Yes | Cancel an appointment |
| `GET` | `/api/appointments/my` | Yes | Fetch logged-in user's appointments |
| `GET` | `/api/appointments/{id}/logs` | Yes | Fetch appointment lifecycle logs |

### Book Appointment Request

```json
{
  "slotId": 1
}
```

### Cancel Appointment Request

```json
{
  "reason": "I am not available at this time"
}
```

### Appointment Response

```json
{
  "appointmentId": 1,
  "slotId": 1,
  "doctorId": 1,
  "doctorName": "Dr. Ananya Rao",
  "specialization": "General Medicine",
  "slotStart": "2026-07-01T09:00:00+05:30",
  "slotEnd": "2026-07-01T09:30:00+05:30",
  "status": "BOOKED",
  "processingStatus": "PENDING",
  "createdAt": "2026-06-21T10:00:00+05:30"
}
```

### Appointment Log Response

```json
[
  {
    "id": 1,
    "eventType": "APPOINTMENT_BOOKED",
    "message": "Appointment booked successfully",
    "createdAt": "2026-06-21T10:00:00+05:30"
  }
]
```

### Book Appointment

```bash
curl -X POST http://localhost:8080/api/appointments \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{"slotId":1}'
```

### Cancel Appointment

```bash
curl -X PATCH http://localhost:8080/api/appointments/1/cancel \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{"reason":"I am not available at this time"}'
```

### Fetch My Appointments

```bash
curl http://localhost:8080/api/appointments/my \
  -H "Authorization: Bearer <token>"
```

### Fetch Appointment Logs

```bash
curl http://localhost:8080/api/appointments/1/logs \
  -H "Authorization: Bearer <token>"
```

## Current Run Commands

Start database:

```bash
docker compose up -d postgres
```

Run backend:

```bash
cd backend
POSTGRES_PORT=5433 mvn spring-boot:run