package com.ubs.expensemanager.dto.expense;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CreateExpenseRequest(
	    UUID employeeId,
	    UUID categoryId,
	    BigDecimal amount,
	    LocalDate date,
	    String currency,
	    String description
) {}
