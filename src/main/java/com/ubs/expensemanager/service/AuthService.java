package com.ubs.expensemanager.service;

import com.ubs.expensemanager.domain.User;
import com.ubs.expensemanager.dto.auth.LoginRequest;
import com.ubs.expensemanager.dto.auth.LoginResponse;
import com.ubs.expensemanager.exception.BusinessException;
import com.ubs.expensemanager.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public AuthService(AuthenticationManager authenticationManager, JwtService jwtService, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    public LoginResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            // load user entity to include id, name, active in response
            User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException("User not found after authentication"));

            // get single role (safely)
            String authority = authentication.getAuthorities().stream()
                .findFirst()
                .map(a -> a.getAuthority())
                .orElse("ROLE_UNSPECIFIED");

            String role = authority.replace("ROLE_", "").toLowerCase();

            String token = jwtService.generateToken(user.getEmail(), role);

            // build response with fields matching DTO
            return new LoginResponse(
                user.getId(),       // UUID
                user.getEmail(),
                user.getName(),
                role,
                user.isActive(),
                token
            );

        } catch (AuthenticationException ex) {
            // map Spring auth exceptions to BusinessException or return 401 upstream
            throw new BusinessException("Invalid email or password");
        }
    }
}
