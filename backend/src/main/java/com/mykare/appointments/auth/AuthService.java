package com.mykare.appointments.auth;

import com.mykare.appointments.auth.dto.AuthResponse;
import com.mykare.appointments.auth.dto.LoginRequest;
import com.mykare.appointments.auth.dto.RegisterRequest;
import com.mykare.appointments.common.ApiException;
import com.mykare.appointments.security.JwtService;
import com.mykare.appointments.security.UserPrincipal;
import com.mykare.appointments.user.User;
import com.mykare.appointments.user.UserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
     
     private final UserRepository userRepository;
     private final PasswordEncoder passwordEncoder;
     private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;


     public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder
        , AuthenticationManager authenticationManager, JwtService jwtService
     ){
            this.userRepository = userRepository;
            this.passwordEncoder = passwordEncoder;
            this.authenticationManager = authenticationManager;
            this.jwtService = jwtService;
     }

     @Transactional
     public AuthResponse register(RegisterRequest request) {
            String normalizedEmail = request.email().trim().toLowerCase();

            if(userRepository.existsByEmail(normalizedEmail)){
                throw new ApiException(HttpStatus.CONFLICT, "User already exists with this email");
            }

            User user = new User(request.fullName().trim(),  normalizedEmail, passwordEncoder.encode(request.password()));

            User savedUser = userRepository.save(user);
            String token = jwtService.generateToken(new UserPrincipal(savedUser));

            return toResponse(savedUser, token, "User Registration successful");
     }

     @Transactional(readOnly = true)
     public AuthResponse login(LoginRequest request){
         String normalizedEmail = request.email().trim().toLowerCase();

         authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        normalizedEmail,
                        request.password()
                )
        );

         User user = userRepository.findByEmail(normalizedEmail)
                    .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));

       String token = jwtService.generateToken(new UserPrincipal(user));

            return toResponse(user, token, "Login successful");
     }

        private AuthResponse toResponse(User user, String token, String message) {
                return new AuthResponse(
                        user.getId(),
                        user.getFullName(),
                        user.getEmail(),
                        user.getRole().name(),
                        token,
                        message
                );
        }
}

