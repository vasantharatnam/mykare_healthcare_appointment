package com.mykare.appointments.appointment;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    boolean existsBySlotIdAndStatus(Long slotId, AppointmentStatus status);

    Optional<Appointment> findByIdAndUserId(Long id, Long userId);

    List<Appointment> findByUserIdOrderByCreatedAtDesc(Long userId);
}