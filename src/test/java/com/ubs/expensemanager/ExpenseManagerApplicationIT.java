package com.ubs.expensemanager;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import com.ubs.expensemanager.integration.AbstractIntegrationTest;

/**
 * Simple integration smoke test to ensure Spring context loads using Testcontainers.
 */
@SpringBootTest
class ExpenseManagerApplicationIT extends AbstractIntegrationTest {

    @Test
    void contextLoads() {
        // If the application context starts and Flyway runs against the Testcontainer DB,
        // this test passes.
    }
}
