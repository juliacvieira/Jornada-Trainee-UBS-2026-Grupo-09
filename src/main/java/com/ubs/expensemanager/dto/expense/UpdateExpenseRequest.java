package com.ubs.expensemanager.dto.expense;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record UpdateExpenseRequest(
	    UUID categoryId,
	    BigDecimal amount,
	    LocalDate date,
	    String currency,
	    String description
) {}
