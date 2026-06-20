package com.mykare.appointments.appointment;

import com.mykare.appointments.appointment.dto.AppointmentResponse;
import com.mykare.appointments.appointment.dto.CreateAppointmentRequest;
import com.mykare.appointments.common.ApiException;
import com.mykare.appointments.slot.AppointmentSlot;
import com.mykare.appointments.slot.AppointmentSlotRepository;
import com.mykare.appointments.user.User;
import com.mykare.appointments.user.UserRepository;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

public class AppointmentService {
    
   private final AppointmentRepository appointmentRepository;
   private final AppointmentLogRepository appointmentLogRepository;
   private final AppointmentSlotRepository appointmentSlotRepository;
   private final UserRepository userRepository;

   public AppointmentService(
              AppointmentRepository appointmentRepository,
              AppointmentLogRepository appointmentLogRepository,
              AppointmentSlotRepository appointmentSlotRepository,
              UserRepository userRepository
   ){
         this.appointmentRepository = appointmentRepository;
         this.appointmentLogRepository = appointmentLogRepository;
         this.appointmentSlotRepository = appointmentSlotRepository;
         this.userRepository = userRepository;
   }

   @Transactional
   public AppointmentResponse bookAppointment(Long userId, CreateAppointmentRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));

        AppointmentSlot slot = appointmentSlotRepository.findById(request.slotId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Appointment slot not found or inactive"));

         if (!slot.getDoctor().isActive()){
              throw new ApiException(HttpStatus.BAD_REQUEST, "Doctor is not active");
         }

         if (appointmentRepository.existsBySlotIdAndStatus(slot.getId(), AppointmentStatus.BOOKED)) {
              throw new ApiException(HttpStatus.CONFLICT, "Appointment slot is already booked");
         }

         try {
             Appointment appointment = new Appointment(user, slot.getDoctor(), slot);
             Appointment savedAppointment = appointmentRepository.save(appointment);

             AppointmentLog log = new AppointmentLog(savedAppointment, "APPOINTMENT_BOOKED" , "Appointment booked successfully");
             appointmentLogRepository.save(log);

             return toResponse(savedAppointment);
         } catch (DataIntegrityViolationException e) {
             throw new ApiException(HttpStatus.CONFLICT, "slot is already booked");
         }
   }

    private AppointmentResponse toResponse(Appointment appointment) {
        return new AppointmentResponse(
                appointment.getId(),
                appointment.getSlot().getId(),
                appointment.getDoctor().getId(),
                appointment.getDoctor().getFullName(),
                appointment.getDoctor().getSpecialization(),
                appointment.getSlot().getSlotStart(),
                appointment.getSlot().getSlotEnd(),
                appointment.getStatus().name(),
                appointment.getProcessingStatus().name(),
                appointment.getCreatedAt()
        );
    }

}
