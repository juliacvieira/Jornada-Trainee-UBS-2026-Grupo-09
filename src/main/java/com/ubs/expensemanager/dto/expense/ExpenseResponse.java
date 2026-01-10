package com.ubs.expensemanager.dto.expense;

import java.util.UUID;
import java.math.BigDecimal;
import java.time.LocalDate;
import com.ubs.expensemanager.domain.Category;
import com.ubs.expensemanager.domain.enums.ExpenseStatus;

public record ExpenseResponse(
	    UUID id,
	    UUID employeeId,
	    String employeeName,
	    UUID categoryId,
	    String categoryName,
	    BigDecimal amount,
	    String currency,
	    LocalDate date,
	    String description,
	    ExpenseStatus status,
	    String receiptUrl,
	    String receiptFilename
	) {}