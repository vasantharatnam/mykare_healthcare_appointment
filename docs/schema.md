# Database Schema

## Tables

| Table | Purpose |
|---|---|
| `users` | Stores registered platform users |
| `doctors` | Stores doctors available for appointments |
| `appointment_slots` | Stores bookable doctor time slots |
| `appointments` | Stores booking and cancellation state |
| `appointment_logs` | Stores appointment lifecycle history |

## Important Constraints

| Constraint | Purpose |
|---|---|
| `users.email UNIQUE` | Prevents duplicate user accounts |
| `appointment_slots(doctor_id, slot_start, slot_end) UNIQUE` | Prevents duplicate slots for same doctor |
| `uk_active_slot_booking` | Prevents duplicate active booking for same slot |
| `chk_appointment_status` | Allows only valid appointment statuses |
| `chk_processing_status` | Allows only valid event-processing statuses |

## Booking Safety

The unique partial index below is used to handle concurrent booking safely:

```sql
CREATE UNIQUE INDEX uk_active_slot_booking
ON appointments(slot_id)
WHERE status = 'BOOKED';