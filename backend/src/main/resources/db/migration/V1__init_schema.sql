CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL DEFAULT 'USER',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE appointment_slots (
    id BIGSERIAL PRIMARY KEY,
    doctor_id BIGINT NOT NULL REFERENCES doctors(id),
    slot_start TIMESTAMPTZ NOT NULL,
    slot_end TIMESTAMPTZ NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_slot_time CHECK (slot_end > slot_start),
    CONSTRAINT uk_doctor_slot UNIQUE (doctor_id, slot_start, slot_end)
);


CREATE TABLE appointments (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    doctor_id BIGINT NOT NULL REFERENCES doctors(id),
    slot_id BIGINT NOT NULL REFERENCES appointment_slots(id),
    status VARCHAR(40) NOT NULL,
    processing_status VARCHAR(40) NOT NULL DEFAULT 'PENDING',
    reason TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    cancelled_at TIMESTAMPTZ,
    CONSTRAINT chk_appointment_status CHECK (
        status IN ('BOOKED', 'CANCELLED')
    ),
    CONSTRAINT chk_processing_status CHECK (
        processing_status IN ('PENDING', 'PROCESSING', 'NOTIFICATION_SENT', 'FAILED')
    )
);

CREATE TABLE appointment_logs (
    id BIGSERIAL PRIMARY KEY,
    appointment_id BIGINT NOT NULL REFERENCES appointments(id),
    event_type VARCHAR(80) NOT NULL,
    message TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);


CREATE UNIQUE INDEX uk_active_slot_booking
ON appointments(slot_id)
WHERE status = 'BOOKED';

CREATE INDEX idx_users_email
ON users(email);

CREATE INDEX idx_slots_doctor_start
ON appointment_slots(doctor_id, slot_start);

CREATE INDEX idx_appointments_user_created
ON appointments(user_id, created_at DESC);

CREATE INDEX idx_appointments_status
ON appointments(status);

CREATE INDEX idx_appointment_logs_appointment
ON appointment_logs(appointment_id, created_at DESC);

INSERT INTO doctors (full_name, specialization)
VALUES
    ('Dr. Ananya Rao', 'General Medicine'),
    ('Dr. Vikram Mehta', 'Cardiology'),
    ('Dr. Priya Nair', 'Dermatology');


INSERT INTO appointment_slots (doctor_id, slot_start, slot_end)
SELECT
    d.id,
    slot_time,
    slot_time + INTERVAL '30 minutes'
FROM doctors d
CROSS JOIN generate_series(
    TIMESTAMPTZ '2026-07-01 09:00:00+05:30',
    TIMESTAMPTZ '2026-07-01 17:00:00+05:30',
    INTERVAL '30 minutes'
) AS slot_time
WHERE slot_time::time < TIME '17:00';