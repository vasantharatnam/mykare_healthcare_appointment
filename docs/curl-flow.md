# Curl Demo Flow

This file contains a complete API test flow.

## 1. Health Check

```bash
curl http://localhost:8080/api/health
```

## 2. Register

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"fullName":"Ratan Kumar","email":"ratan@example.com","password":"password123"}'
```

Copy the `token` from response.

## 3. Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"ratan@example.com","password":"password123"}'
```

## 4. Fetch Doctors

```bash
curl http://localhost:8080/api/doctors \
  -H "Authorization: Bearer <token>"
```

## 5. Fetch Available Slots

```bash
curl "http://localhost:8080/api/slots/available?date=2026-07-01" \
  -H "Authorization: Bearer <token>"
```

## 6. Book Appointment

```bash
curl -X POST http://localhost:8080/api/appointments \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{"slotId":1}'
```

## 7. Fetch My Appointments

```bash
curl http://localhost:8080/api/appointments/my \
  -H "Authorization: Bearer <token>"
```

## 8. Fetch Appointment Logs

```bash
curl http://localhost:8080/api/appointments/1/logs \
  -H "Authorization: Bearer <token>"
```

## 9. Cancel Appointment

```bash
curl -X PATCH http://localhost:8080/api/appointments/1/cancel \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{"reason":"Not available at this time"}'
```

## 10. Verify Duplicate Booking Protection

Try booking the same slot twice:

```bash
curl -X POST http://localhost:8080/api/appointments \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{"slotId":1}'
```

Expected error:

```json
{
  "status": 409,
  "error": "Conflict",
  "messages": [
    "Appointment slot is already booked"
  ]
}
```