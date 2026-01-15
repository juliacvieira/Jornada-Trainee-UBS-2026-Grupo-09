package com.ubs.expensemanager.dto.report;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.ubs.expensemanager.domain.enums.ExpenseStatus;

public record ExpenseReportRow(
        UUID id,
        LocalDate date,
        String employeeName,
        String departmentName,
        String categoryName,
        BigDecimal amount,
        String currency,
        ExpenseStatus status,
        boolean needsReview
) {}