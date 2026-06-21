# Python Notification Worker

Consumes appointment events from Kafka, simulates notification delivery, and updates appointment processing status in PostgreSQL.

## Responsibilities

- Consume events from `appointment-events`
- Mark appointment as `PROCESSING`
- Simulate notification delivery
- Mark appointment as `NOTIFICATION_SENT`
- Insert appointment logs
- Mark appointment as `FAILED` if processing fails

## Setup

Create virtual environment:

```bash
cd worker
python3 -m venv .venv
source .venv/bin/activate