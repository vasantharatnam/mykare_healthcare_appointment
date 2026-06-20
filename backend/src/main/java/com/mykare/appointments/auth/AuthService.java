package com.mykare.appointments.auth;

import com.mykare.appointments.auth.dto.AuthResponse;
import com.mykare.appointments.auth.dto.LoginRequest;
import com.mykare.appointments.auth.dto.RegisterRequest;
import com.mykare.appointments.common.ApiException;
import com.mykare.appointments.user.User;
import com.mykare.appointments.user.UserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
     
     private final UserRepository userRepository;
     private final PasswordEncoder passwordEncoder;


     public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder){
            this.userRepository = userRepository;
            this.passwordEncoder = passwordEncoder;
     }

     @Transactional
     public AuthResponse register(RegisterRequest request) {
            String normalizedEmail = request.email().trim().toLowerCase();

            if(userRepository.existsByEmail(normalizedEmail)){
                throw new ApiException(HttpStatus.CONFLICT, "User already exists with this email");
            }

            User user = new User(request.fullName().trim(),  normalizedEmail, passwordEncoder.encode(request.password()));

            User savedUser = userRepository.save(user);

            return toResponse(savedUser, "User Registration successful");
     }

     @Transactional(readOnly = true)
     public AuthResponse login(LoginRequest request){
         String normalizedEmail = request.email().trim().toLowerCase();

         User user = userRepository.findByEmail(normalizedEmail)
                    .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));

         if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
             throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
         }

            return toResponse(user, "Login successful");
     }

        private AuthResponse toResponse(User user, String message) {
                return new AuthResponse(
                        user.getId(),
                        user.getFullName(),
                        user.getEmail(),
                        user.getRole().name(),
                        message
                );
        }
}

