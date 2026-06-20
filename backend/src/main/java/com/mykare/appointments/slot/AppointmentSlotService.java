package com.mykare.appointments.slot;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import com.mykare.appointments.slot.dto.AvailableSlotResponse;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AppointmentSlotService {

    private static final ZoneOffset DEFAULT_OFFSET = ZoneOffset.of("+05:30");

    private final AppointmentSlotRepository appointmentSlotRepository;

    public AppointmentSlotService(AppointmentSlotRepository appointmentSlotRepository) {
        this.appointmentSlotRepository = appointmentSlotRepository;
    }

    @Transactional(readOnly = true)
    public List<AvailableSlotResponse> getAvailableSlots(Long doctorId, LocalDate date) {
        OffsetDateTime start;
        OffsetDateTime end;

        if (date == null) {
            start = OffsetDateTime.now(DEFAULT_OFFSET);
            end = start.plusDays(7);
        } else {
            start = date.atStartOfDay().atOffset(DEFAULT_OFFSET);
            end = start.plusDays(1);
        }

        return appointmentSlotRepository.findAvailableSlots(doctorId, start, end)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private AvailableSlotResponse toResponse(AppointmentSlot slot) {
        return new AvailableSlotResponse(
                slot.getId(),
                slot.getDoctor().getId(),
                slot.getDoctor().getFullName(),
                slot.getDoctor().getSpecialization(),
                slot.getSlotStart(),
                slot.getSlotEnd()
        );
    }
}