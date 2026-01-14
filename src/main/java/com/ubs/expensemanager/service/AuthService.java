package com.ubs.expensemanager.service;
import com.ubs.expensemanager.domain.User;
import com.ubs.expensemanager.dto.auth.LoginRequest;
import com.ubs.expensemanager.dto.auth.LoginResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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

    User user = (User) authentication.getPrincipal();

    String role = authentication.getAuthorities()
        .iterator()
        .next()
        .getAuthority()
        .replace("ROLE_", "")
        .toUpperCase();

    String token = jwtService.generateToken(
        user.getEmail(),
        role
    );

    return new LoginResponse(
        user.getId(),
        user.getEmail(),
        user.getName(),
        role,
        user.isActive(),
        token
    );
  }
}