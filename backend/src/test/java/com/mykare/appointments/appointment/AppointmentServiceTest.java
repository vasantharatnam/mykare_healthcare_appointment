package com.mykare.appointments.appointment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import com.mykare.appointments.appointment.dto.AppointmentResponse;
import com.mykare.appointments.appointment.dto.CancelAppointmentRequest;
import com.mykare.appointments.appointment.dto.CreateAppointmentRequest;
import com.mykare.appointments.common.ApiException;
import com.mykare.appointments.doctor.Doctor;
import com.mykare.appointments.event.AppointmentEventFactory;
import com.mykare.appointments.slot.AppointmentSlot;
import com.mykare.appointments.slot.AppointmentSlotRepository;
import com.mykare.appointments.user.User;
import com.mykare.appointments.user.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

class AppointmentServiceTest {

    private AppointmentRepository appointmentRepository;
    private AppointmentLogRepository appointmentLogRepository;
    private AppointmentSlotRepository appointmentSlotRepository;
    private UserRepository userRepository;
    private ApplicationEventPublisher applicationEventPublisher;
    private AppointmentService appointmentService;

    @BeforeEach
    void setUp() {
        appointmentRepository = mock(AppointmentRepository.class);
        appointmentLogRepository = mock(AppointmentLogRepository.class);
        appointmentSlotRepository = mock(AppointmentSlotRepository.class);
        userRepository = mock(UserRepository.class);
        applicationEventPublisher = mock(ApplicationEventPublisher.class);

        AppointmentEventFactory appointmentEventFactory = new AppointmentEventFactory();

        appointmentService = new AppointmentService(
                appointmentRepository,
                appointmentLogRepository,
                appointmentSlotRepository,
                userRepository,
                applicationEventPublisher,
                appointmentEventFactory
        );
    }

    @Test
    void shouldBookAppointment() {
        User user = user(1L);
        Doctor doctor = doctor(1L, "Dr. Ananya Rao", "General Medicine", true);
        AppointmentSlot slot = slot(1L, doctor);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(appointmentSlotRepository.findById(any(Long.class))).thenReturn(Optional.of(slot));
        when(appointmentRepository.existsBySlotIdAndStatus(1L, AppointmentStatus.BOOKED)).thenReturn(false);

        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(invocation -> savedAppointment(invocation.getArgument(0)));
        when(appointmentRepository.saveAndFlush(any(Appointment.class))).thenAnswer(invocation -> savedAppointment(invocation.getArgument(0)));

        AppointmentResponse response = appointmentService.bookAppointment(
                1L,
                new CreateAppointmentRequest(1L)
        );

        assertThat(response.appointmentId()).isEqualTo(10L);
        assertThat(response.slotId()).isEqualTo(1L);
        assertThat(response.doctorName()).isEqualTo("Dr. Ananya Rao");
        assertThat(response.status()).isEqualTo("BOOKED");
        assertThat(response.processingStatus()).isEqualTo("PENDING");

        verify(appointmentLogRepository).save(any(AppointmentLog.class));
        verify(applicationEventPublisher).publishEvent(any(Object.class));
    }

    @Test
    void shouldRejectAlreadyBookedSlot() {
        User user = user(1L);
        Doctor doctor = doctor(1L, "Dr. Ananya Rao", "General Medicine", true);
        AppointmentSlot slot = slot(1L, doctor);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(appointmentSlotRepository.findById(any(Long.class))).thenReturn(Optional.of(slot));
        when(appointmentRepository.existsBySlotIdAndStatus(1L, AppointmentStatus.BOOKED)).thenReturn(true);

        assertThatThrownBy(() -> appointmentService.bookAppointment(1L, new CreateAppointmentRequest(1L)))
                .isInstanceOf(ApiException.class)
                .hasMessage("Appointment slot is already booked");

        verify(appointmentRepository, never()).save(any(Appointment.class));
        verify(appointmentRepository, never()).saveAndFlush(any(Appointment.class));
    }

    @Test
    void shouldCancelAppointment() {
        User user = user(1L);
        Doctor doctor = doctor(1L, "Dr. Ananya Rao", "General Medicine", true);
        AppointmentSlot slot = slot(1L, doctor);
        Appointment appointment = appointment(10L, user, doctor, slot);

        when(appointmentRepository.findByIdAndUserId(10L, 1L)).thenReturn(Optional.of(appointment));

        AppointmentResponse response = appointmentService.cancelAppointment(
                1L,
                10L,
                new CancelAppointmentRequest("Not available")
        );

        assertThat(response.appointmentId()).isEqualTo(10L);
        assertThat(response.status()).isEqualTo("CANCELLED");

        verify(appointmentLogRepository).save(any(AppointmentLog.class));
        verify(applicationEventPublisher).publishEvent(any(Object.class));
    }

    @Test
    void shouldReturnMyAppointments() {
        User user = user(1L);
        Doctor doctor = doctor(1L, "Dr. Ananya Rao", "General Medicine", true);
        AppointmentSlot slot = slot(1L, doctor);
        Appointment appointment = appointment(10L, user, doctor, slot);

        when(appointmentRepository.findByUserIdOrderByCreatedAtDesc(1L))
                .thenReturn(List.of(appointment));

        List<AppointmentResponse> response = appointmentService.getMyAppointments(1L);

        assertThat(response).hasSize(1);
        assertThat(response.get(0).appointmentId()).isEqualTo(10L);
    }

    private Appointment savedAppointment(Appointment appointment) {
        setField(appointment, "id", 10L);
        setField(appointment, "createdAt", OffsetDateTime.now());
        setField(appointment, "updatedAt", OffsetDateTime.now());
        return appointment;
    }

    private User user(Long id) {
        User user = new User("Ratan Kumar", "ratan@example.com", "hashed-password");
        setField(user, "id", id);
        return user;
    }

    private Doctor doctor(Long id, String fullName, String specialization, boolean active) {
        Doctor doctor = newInstance(Doctor.class);
        setField(doctor, "id", id);
        setField(doctor, "fullName", fullName);
        setField(doctor, "specialization", specialization);
        setField(doctor, "active", active);
        return doctor;
    }

    private AppointmentSlot slot(Long id, Doctor doctor) {
        AppointmentSlot slot = newInstance(AppointmentSlot.class);
        setField(slot, "id", id);
        setField(slot, "doctor", doctor);
        setField(slot, "slotStart", OffsetDateTime.parse("2026-07-01T09:00:00+05:30"));
        setField(slot, "slotEnd", OffsetDateTime.parse("2026-07-01T09:30:00+05:30"));
        setField(slot, "active", true);
        return slot;
    }

    private Appointment appointment(Long id, User user, Doctor doctor, AppointmentSlot slot) {
        Appointment appointment = new Appointment(user, doctor, slot);
        setField(appointment, "id", id);
        setField(appointment, "createdAt", OffsetDateTime.now());
        setField(appointment, "updatedAt", OffsetDateTime.now());
        return appointment;
    }

    private static <T> T newInstance(Class<T> type) {
        try {
            var constructor = type.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to create instance of " + type.getSimpleName(), ex);
        }
    }

    private static void setField(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to set field " + fieldName, ex);
        }
    }
}