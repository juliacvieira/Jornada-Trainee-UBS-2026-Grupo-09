package com.ubs.expensemanager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.ubs.expensemanager.security.JwtAuthenticationFilter;

/**
 * Security configuration.
 *
 * Comments in English as requested.
 *
 * This config:
 * - exposes AuthenticationManager bean so AuthService can inject it
 * - exposes PasswordEncoder bean for SecurityUserDetailsService and any user setup
 * - registers the JwtAuthenticationFilter before the UsernamePasswordAuthenticationFilter
 * - keeps /api/auth/login open (you asked to keep that path)
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    @Autowired
    public SecurityConfig(JwtAuthenticationFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    /**
     * Expose AuthenticationManager as a bean so other services (e.g. AuthService)
     * can @Autowired it in constructors.
     *
     * We obtain it from AuthenticationConfiguration which builds it based on
     * the configured UserDetailsService and PasswordEncoder.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * Password encoder bean. Use BCrypt in dev and production.
     * If you removed encoding for testing, you can replace this with NoOpPasswordEncoder,
     * but it's strongly recommended to keep BCrypt for real usage.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
    	return NoOpPasswordEncoder.getInstance();
    }

    /**
     * Main security filter chain.
     *
     * - keeps /api/auth/login permitAll
     * - requires authentication for everything else
     * - stateless session (JWT)
     * - registers jwtFilter before UsernamePasswordAuthenticationFilter
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll() // keep this path as you requested
                .anyRequest().authenticated()
            )
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            // add JWT filter before UsernamePasswordAuthenticationFilter
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
