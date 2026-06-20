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

## Current Run Commands

Start database:

```bash
docker compose up -d postgres
```

Run backend:

```bash
cd backend
POSTGRES_PORT=5433 mvn spring-boot:run