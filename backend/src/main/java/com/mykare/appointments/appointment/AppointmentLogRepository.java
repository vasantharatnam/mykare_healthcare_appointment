package com.mykare.appointments.appointment;

import java.util.List;


import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentLogRepository extends JpaRepository<AppointmentLog, Long> {

  List<AppointmentLog> findByAppointmentIdOrderByCreatedAtDesc(Long appointmentId);

}