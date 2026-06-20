package com.mykare.appointments.doctor;


import java.util.List;

import com.mykare.appointments.doctor.dto.DoctorResponse;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class DoctorService {
     
    private final DoctorRepository doctorRepository;

    public DoctorService(DoctorRepository doctorRepository){
        this.doctorRepository = doctorRepository;
    }

    @Transactional(readOnly = true)
    public List<DoctorResponse> getActiveDoctors() {
         return doctorRepository.findByActiveTrueOrderByFullNameAsc()
                         .stream()
                         .map(this::toResponse)
                         .toList();
    }

     private DoctorResponse toResponse(Doctor doctor) {
        return new DoctorResponse(
                doctor.getId(),
                doctor.getFullName(),
                doctor.getSpecialization()
        );
    }
}

