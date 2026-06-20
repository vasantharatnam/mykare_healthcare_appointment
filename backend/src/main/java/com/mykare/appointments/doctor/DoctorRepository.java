package com.mykare.appointments.doctor;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
     List<Doctor> findByActiveTrueOrderByFullNameAsc();
}
