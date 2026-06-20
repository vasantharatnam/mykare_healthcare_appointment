package com.mykare.appointments.slot;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AppointmentSlotRepository extends JpaRepository<AppointmentSlot, Long> {

    @Query(value = """
            SELECT s.*
            FROM appointment_slots s
            JOIN doctors d ON d.id = s.doctor_id
            WHERE s.active = true
              AND d.active = true
              AND s.slot_start >= :start
              AND s.slot_start < :end
              AND (:doctorId IS NULL OR d.id = :doctorId)
              AND NOT EXISTS (
                    SELECT 1
                    FROM appointments a
                    WHERE a.slot_id = s.id
                      AND a.status = 'BOOKED'
              )
            ORDER BY s.slot_start ASC, d.full_name ASC
            """, nativeQuery = true)
    List<AppointmentSlot> findAvailableSlots(
            @Param("doctorId") Long doctorId,
            @Param("start") OffsetDateTime start,
            @Param("end") OffsetDateTime end
    );
}