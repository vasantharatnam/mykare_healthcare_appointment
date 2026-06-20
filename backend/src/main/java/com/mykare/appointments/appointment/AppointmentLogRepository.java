package com.mykare.appointments.appointment;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentLogRepository extends JpaRepository<AppointmentLog, Long> {
}