package com.mykare.appointments.doctor;

import java.util.List;

import com.mykare.appointments.doctor.dto.DoctorResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/doctors")
public class DoctorController {
    
    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService){
        this.doctorService = doctorService;
    }

    @GetMapping
    public ResponseEntity<List<DoctorResponse>> getDoctors() {
        return ResponseEntity.ok(doctorService.getActiveDoctors());
    }

}
