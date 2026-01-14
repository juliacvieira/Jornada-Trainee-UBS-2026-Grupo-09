package com.ubs.expensemanager.controller;

import com.ubs.expensemanager.dto.auth.LoginRequest;
import com.ubs.expensemanager.dto.auth.LoginResponse;
import com.ubs.expensemanager.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }
}
