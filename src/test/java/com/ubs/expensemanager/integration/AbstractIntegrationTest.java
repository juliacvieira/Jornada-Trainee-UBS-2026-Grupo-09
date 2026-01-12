package com.ubs.expensemanager.integration;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.boot.test.context.SpringBootTest;

@Testcontainers
@SpringBootTest
public abstract class AbstractIntegrationTest {

    /**
     * PostgreSQL container managed by Testcontainers + JUnit Jupiter.
     * The lifecycle (start/stop) is automatically handled by the framework,
     * avoiding manual resource management and IDE "resource leak" warnings.
     */
    @Container
    static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:15-alpine")
                    .withDatabaseName("expense_manager_test")
                    .withUsername("test")
                    .withPassword("test");

    /**
     * Inject dynamic datasource properties into the Spring context
     * before it is initialized, so Flyway and JPA connect to the
     * Testcontainers PostgreSQL instance instead of localhost.
     */
    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name",
                () -> "org.postgresql.Driver");

        // Ensure Flyway is enabled for integration tests
        registry.add("spring.flyway.enabled", () -> "true");
    }
}
