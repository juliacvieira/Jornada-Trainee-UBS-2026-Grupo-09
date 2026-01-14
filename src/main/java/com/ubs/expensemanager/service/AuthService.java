package com.ubs.expensemanager.service;

import com.ubs.expensemanager.domain.User;
import com.ubs.expensemanager.dto.auth.LoginRequest;
import com.ubs.expensemanager.dto.auth.LoginResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import com.ubs.expensemanager.exception.BusinessException;
import com.ubs.expensemanager.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;

  public AuthService(AuthenticationManager authenticationManager, JwtService jwtService) {
    this.authenticationManager = authenticationManager;
    this.jwtService = jwtService;
  }

  public LoginResponse login(LoginRequest request) {
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            request.getEmail(),
            request.getPassword()
        )
    );

    String authority = authentication.getAuthorities()
        .iterator()
        .next()
        .getAuthority();

    String role = authority.replace("ROLE_", "").toLowerCase();

    String token = jwtService.generateToken(
        request.getEmail(),
        role
    );

    return new LoginResponse(
        request.getEmail(),
        role,
        token
    );
  }
}