package com.mykare.appointments.security;

import com.mykare.appointments.user.User;
import com.mykare.appointments.user.UserRepository;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class UserPrincipalService implements UserDetailsService {
       
     private final UserRepository userRepository;

     public UserPrincipalService(UserRepository userRepository) {
            this.userRepository = userRepository;
     }

     @Override
     @Transactional(readOnly = true)
     public UserDetails loadUserByUsername(String email) {
        User user = userRepository.findByEmail(email.toLowerCase())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        return new UserPrincipal(user);
     }
}
