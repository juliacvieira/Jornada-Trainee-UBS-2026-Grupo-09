package com.ubs.expensemanager.service;

import com.ubs.expensemanager.domain.User;
import com.ubs.expensemanager.dto.auth.LoginRequest;
import com.ubs.expensemanager.dto.auth.LoginResponse;
import com.ubs.expensemanager.exception.BusinessException;
import com.ubs.expensemanager.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmailAndActive(request.getEmail(), true)
                .orElseThrow(() -> new BusinessException("Invalid email or password"));

        // Simple password validation (should use BCrypt in production)
        if (!user.getPassword().equals(request.getPassword())) {
            throw new BusinessException("Invalid email or password");
        }

        return new LoginResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getRole(),
                user.isActive()
        );
    }
}
