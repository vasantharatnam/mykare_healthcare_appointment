# API Documentation Notes

Swagger/OpenAPI will be exposed by the Spring Boot service.

Planned URL:

```text
http://localhost:8080/swagger-ui.html

## Auth APIs

| Method | Path | Purpose |
|---|---|---|
| `POST` | `/api/auth/register` | Register a new user |
| `POST` | `/api/auth/login` | Login existing user |

### Register Request

```json
{
  "fullName": "Ratan Kumar",
  "email": "ratan@example.com",
  "password": "password123"
}