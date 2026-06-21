package com.mykare.appointments.appointment;

import java.util.List;

import com.mykare.appointments.appointment.dto.AppointmentLogResponse;
import com.mykare.appointments.appointment.dto.AppointmentResponse;
import com.mykare.appointments.appointment.dto.CancelAppointmentRequest;
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

import com.mykare.appointments.event.AppointmentEventFactory;
import com.mykare.appointments.event.AppointmentEventType;

import org.springframework.context.ApplicationEventPublisher;


@Service
public class AppointmentService {
    
   private final AppointmentRepository appointmentRepository;
   private final AppointmentLogRepository appointmentLogRepository;
   private final AppointmentSlotRepository appointmentSlotRepository;
   private final UserRepository userRepository;
   private final ApplicationEventPublisher applicationEventPublisher;
   private final AppointmentEventFactory appointmentEventFactory;

   public AppointmentService(
              AppointmentRepository appointmentRepository,
              AppointmentLogRepository appointmentLogRepository,
              AppointmentSlotRepository appointmentSlotRepository,
              UserRepository userRepository,
              ApplicationEventPublisher applicationEventPublisher,
              AppointmentEventFactory appointmentEventFactory
   ){
         this.appointmentRepository = appointmentRepository;
         this.appointmentLogRepository = appointmentLogRepository;
         this.appointmentSlotRepository = appointmentSlotRepository;
         this.userRepository = userRepository;
         this.applicationEventPublisher = applicationEventPublisher;
         this.appointmentEventFactory = appointmentEventFactory;
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

             applicationEventPublisher.publishEvent( appointmentEventFactory.create(savedAppointment, AppointmentEventType.APPOINTMENT_BOOKED));

             return toResponse(savedAppointment);
         } catch (DataIntegrityViolationException e) {
             throw new ApiException(HttpStatus.CONFLICT, "slot is already booked");
         }
   }

   @Transactional
   public AppointmentResponse cancelAppointment(Long userId, Long appointmentId, CancelAppointmentRequest  request){
       Appointment appointment = appointmentRepository.findByIdAndUserId(appointmentId, userId)
                                  .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Appointment not found"));
       
       if (appointment.getStatus() ==  AppointmentStatus.CANCELLED){
         throw new ApiException(HttpStatus.BAD_REQUEST, "Appointment is already cancelled");
       }        
       
       String reason = request.reason() == null || request.reason().isBlank() ? "Cancelled by user" : request.reason().trim();

       appointment.cancel(reason);

       AppointmentLog log = new AppointmentLog(
                appointment,
                "APPOINTMENT_CANCELLED",
                reason
        );

        appointmentLogRepository.save(log);

        applicationEventPublisher.publishEvent( appointmentEventFactory.create(appointment, AppointmentEventType.APPOINTMENT_CANCELLED));

        return toResponse(appointment);
   }

   @Transactional(readOnly = true)
    public List<AppointmentResponse> getMyAppointments(Long userId) {
        return appointmentRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AppointmentLogResponse> getAppointmentLogs(Long userId, Long appointmentId) {
        Appointment appointment = appointmentRepository.findByIdAndUserId(appointmentId, userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Appointment not found"));

        return appointmentLogRepository.findByAppointmentIdOrderByCreatedAtDesc(appointment.getId())
                .stream()
                .map(this::toLogResponse)
                .toList();
    }

 private AppointmentLogResponse toLogResponse(AppointmentLog log) {
        return new AppointmentLogResponse(
                log.getId(),
                log.getEventType(),
                log.getMessage(),
                log.getCreatedAt()
        );
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
