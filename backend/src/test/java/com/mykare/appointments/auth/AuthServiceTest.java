package com.mykare.appointments.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;

import com.mykare.appointments.auth.dto.AuthResponse;
import com.mykare.appointments.auth.dto.LoginRequest;
import com.mykare.appointments.auth.dto.RegisterRequest;
import com.mykare.appointments.common.ApiException;
import com.mykare.appointments.security.JwtService;
import com.mykare.appointments.user.User;
import com.mykare.appointments.user.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

class AuthServiceTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        authenticationManager = mock(AuthenticationManager.class);

        JwtService jwtService = new JwtService(
                "ARj4ptWyFeD26g9nQ8fjeTjLRKjjTcRxzstEfgC6cy8",
                1440
        );

        authService = new AuthService(
                userRepository,
                passwordEncoder,
                authenticationManager,
                jwtService
        );
    }

    @Test
    void shouldRegisterUserAndReturnToken() {
        RegisterRequest request = new RegisterRequest(
                "Ratan Kumar",
                "RATAN@example.com",
                "password123"
        );

        User savedUser = new User(
                "Ratan Kumar",
                "ratan@example.com",
                "hashed-password"
        );

        setField(savedUser, "id", 1L);

        when(userRepository.existsByEmail("ratan@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashed-password");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        AuthResponse response = authService.register(request);

        assertThat(response.userId()).isEqualTo(1L);
        assertThat(response.fullName()).isEqualTo("Ratan Kumar");
        assertThat(response.email()).isEqualTo("ratan@example.com");
        assertThat(response.role()).isEqualTo("USER");
        assertThat(response.token()).isNotBlank();
        assertThat(response.message()).isEqualTo("User registered successfully");

        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldRejectDuplicateEmailDuringRegistration() {
        RegisterRequest request = new RegisterRequest(
                "Ratan Kumar",
                "ratan@example.com",
                "password123"
        );

        when(userRepository.existsByEmail("ratan@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(ApiException.class)
                .hasMessage("User already exists with this email");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldLoginAndReturnToken() {
        LoginRequest request = new LoginRequest(
                "RATAN@example.com",
                "password123"
        );

        User user = new User(
                "Ratan Kumar",
                "ratan@example.com",
                "hashed-password"
        );

        setField(user, "id", 1L);

        when(userRepository.findByEmail("ratan@example.com")).thenReturn(Optional.of(user));

        AuthResponse response = authService.login(request);

        assertThat(response.userId()).isEqualTo(1L);
        assertThat(response.email()).isEqualTo("ratan@example.com");
        assertThat(response.role()).isEqualTo("USER");
        assertThat(response.token()).isNotBlank();
        assertThat(response.message()).isEqualTo("Login successful");

        verify(authenticationManager).authenticate(any());
    }

    private static void setField(Object target, String fieldName, Object value) {
        try {
            var field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to set field " + fieldName, ex);
        }
    }
}