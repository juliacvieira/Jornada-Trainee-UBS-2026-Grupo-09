package com.ubs.expensemanager.service;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import com.ubs.expensemanager.domain.User;
import com.ubs.expensemanager.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class SecurityUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public SecurityUserDetailsService(UserRepository userRepository,
                                    PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public UserDetails loadUserByUsername(String email) {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    return org.springframework.security.core.userdetails.User
        .withUsername(user.getEmail())
        .password(passwordEncoder.encode(user.getPassword())) // mock ok
        .roles(user.getRole().toUpperCase())
        .build();
  }
}
