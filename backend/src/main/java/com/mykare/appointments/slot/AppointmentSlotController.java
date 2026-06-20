package com.mykare.appointments.slot;

import java.time.LocalDate;
import java.util.List;

import com.mykare.appointments.slot.dto.AvailableSlotResponse;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/slots")
public class AppointmentSlotController {

    private final AppointmentSlotService appointmentSlotService;

    public AppointmentSlotController(AppointmentSlotService appointmentSlotService) {
        this.appointmentSlotService = appointmentSlotService;
    }

    @GetMapping("/available")
    public ResponseEntity<List<AvailableSlotResponse>> getAvailableSlots(
            @RequestParam(required = false) Long doctorId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    ) {
        return ResponseEntity.ok(appointmentSlotService.getAvailableSlots(doctorId, date));
    }
}