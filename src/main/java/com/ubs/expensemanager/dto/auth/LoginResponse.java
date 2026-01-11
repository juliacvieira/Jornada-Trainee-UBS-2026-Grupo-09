package com.ubs.expensemanager.dto.auth;

import java.util.UUID;

public class LoginResponse {
    private UUID id;
    private String email;
    private String name;
    private String role;
    private boolean active;

    public LoginResponse() {}

    public LoginResponse(UUID id, String email, String name, String role, boolean active) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.role = role;
        this.active = active;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
