# Demo Checklist

## Before Demo

- Docker Desktop is running
- Port `8080` is free
- Port `5173` is free
- Port `5433` is free
- `.env` has correct values
- Latest code is pushed to GitHub

## Start System

```bash
docker compose down -v
docker compose up --build

## Final Sanity Check

Before submitting:

- `docker compose up --build` starts successfully
- Frontend opens on `http://localhost:5173`
- Backend health returns `UP`
- Swagger opens correctly
- Register/login works
- Slots are visible for `2026-07-01`
- Appointment booking works
- Duplicate booking is blocked
- Worker logs show notification processing
- Appointment logs show booking and notification events
- Cancellation works
- GitHub repository is public or accessible to reviewer