# Database Schema

The database schema is managed by Flyway.

Migration file:

```text
backend/src/main/resources/db/migration/V1__init_schema.sql
```

## Tables

| Table | Purpose |
|---|---|
| `users` | Stores registered users |
| `doctors` | Stores doctors available for appointments |
| `appointment_slots` | Stores available time slots for doctors |
| `appointments` | Stores appointment booking/cancellation state |
| `appointment_logs` | Stores lifecycle logs for each appointment |
| `flyway_schema_history` | Tracks applied Flyway migrations |

## users

| Column | Type | Notes |
|---|---|---|
| `id` | `BIGSERIAL` | Primary key |
| `full_name` | `VARCHAR(120)` | User full name |
| `email` | `VARCHAR(160)` | Unique user email |
| `password_hash` | `VARCHAR(255)` | BCrypt hashed password |
| `role` | `VARCHAR(30)` | Currently `USER` |
| `created_at` | `TIMESTAMPTZ` | Creation timestamp |
| `updated_at` | `TIMESTAMPTZ` | Last update timestamp |

Important constraint:

```sql
email UNIQUE
```

## doctors

| Column | Type | Notes |
|---|---|---|
| `id` | `BIGSERIAL` | Primary key |
| `full_name` | `VARCHAR(120)` | Doctor name |
| `specialization` | `VARCHAR(120)` | Doctor specialization |
| `active` | `BOOLEAN` | Whether doctor is available |
| `created_at` | `TIMESTAMPTZ` | Creation timestamp |
| `updated_at` | `TIMESTAMPTZ` | Last update timestamp |

## appointment_slots

| Column | Type | Notes |
|---|---|---|
| `id` | `BIGSERIAL` | Primary key |
| `doctor_id` | `BIGINT` | Foreign key to `doctors(id)` |
| `slot_start` | `TIMESTAMPTZ` | Slot start time |
| `slot_end` | `TIMESTAMPTZ` | Slot end time |
| `active` | `BOOLEAN` | Whether slot is available |
| `created_at` | `TIMESTAMPTZ` | Creation timestamp |
| `updated_at` | `TIMESTAMPTZ` | Last update timestamp |

Important constraints:

```sql
FOREIGN KEY doctor_id REFERENCES doctors(id)
CHECK (slot_end > slot_start)
UNIQUE (doctor_id, slot_start, slot_end)
```

## appointments

| Column | Type | Notes |
|---|---|---|
| `id` | `BIGSERIAL` | Primary key |
| `user_id` | `BIGINT` | Foreign key to `users(id)` |
| `doctor_id` | `BIGINT` | Foreign key to `doctors(id)` |
| `slot_id` | `BIGINT` | Foreign key to `appointment_slots(id)` |
| `status` | `VARCHAR(40)` | `BOOKED` or `CANCELLED` |
| `processing_status` | `VARCHAR(40)` | Worker processing state |
| `reason` | `TEXT` | Cancellation reason |
| `created_at` | `TIMESTAMPTZ` | Creation timestamp |
| `updated_at` | `TIMESTAMPTZ` | Last update timestamp |
| `cancelled_at` | `TIMESTAMPTZ` | Cancellation timestamp |

Valid appointment statuses:

```text
BOOKED
CANCELLED
```

Valid processing statuses:

```text
PENDING
PROCESSING
NOTIFICATION_SENT
FAILED
```

## appointment_logs

| Column | Type | Notes |
|---|---|---|
| `id` | `BIGSERIAL` | Primary key |
| `appointment_id` | `BIGINT` | Foreign key to `appointments(id)` |
| `event_type` | `VARCHAR(80)` | Lifecycle event name |
| `message` | `TEXT` | Human-readable log message |
| `created_at` | `TIMESTAMPTZ` | Log creation timestamp |

## Important Indexes

| Index | Purpose |
|---|---|
| `idx_users_email` | Fast lookup by email |
| `idx_slots_doctor_start` | Fast slot filtering by doctor and start time |
| `idx_appointments_user_created` | Fast user appointment history |
| `idx_appointments_status` | Fast appointment status filtering |
| `idx_appointment_logs_appointment` | Fast appointment log lookup |
| `uk_active_slot_booking` | Prevent duplicate active slot booking |

## Duplicate Booking Protection

The most important index:

```sql
CREATE UNIQUE INDEX uk_active_slot_booking
ON appointments(slot_id)
WHERE status = 'BOOKED';
```

This allows historical cancelled appointments to remain in the database, but prevents more than one active booking for the same slot.

Example:

| slot_id | status | Allowed? |
|---|---|---|
| `1` | `BOOKED` | Yes |
| `1` | `BOOKED` | No |
| `1` | `CANCELLED` | Yes |

## Seed Data

The first migration inserts sample doctors:

```text
Dr. Ananya Rao - General Medicine
Dr. Vikram Mehta - Cardiology
Dr. Priya Nair - Dermatology
```

It also creates 30-minute slots for:

```text
2026-07-01 09:00 to 17:00 IST
```