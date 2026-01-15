package com.ubs.expensemanager.config;

import com.ubs.expensemanager.domain.User;
import com.ubs.expensemanager.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initUsers(UserRepository userRepository) {
        return args -> {
            createIfMissing(userRepository, "employee@ubs.com", "123456", "Employee User", "EMPLOYEE");
            createIfMissing(userRepository, "manager@ubs.com", "123456", "Manager User", "MANAGER");
            createIfMissing(userRepository, "finance@ubs.com", "123456", "Finance User", "FINANCE");
        };
    }

    private void createIfMissing(UserRepository repo, String email, String password, String name, String role) {
        repo.findByEmail(email).orElseGet(() -> {
            User u = new User();
            u.setEmail(email);
            u.setPassword(password); // NOTE: Plain for demo; use BCrypt in prod
            u.setName(name);
            u.setRole(role);
            u.setActive(true);
            return repo.save(u);
        });
    }
}
