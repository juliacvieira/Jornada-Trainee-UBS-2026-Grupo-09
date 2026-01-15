package com.ubs.expensemanager.service;

import com.ubs.expensemanager.domain.User;
import com.ubs.expensemanager.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Adapter between domain User entity and Spring Security.
 *
 * This simplified version assumes passwords are stored in plain text
 * (e.g. "123456") and compares them directly.
 *
 * This is acceptable for development/testing only.
 */
@Service
public class SecurityUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public SecurityUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        return org.springframework.security.core.userdetails.User
            .withUsername(user.getEmail())
            .password(user.getPassword()) // plain text password
            .roles(user.getRole().toUpperCase())
            .disabled(!user.isActive())
            .build();
    }
}
