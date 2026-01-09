package com.ubs.expensemanager.dto;

import java.util.UUID;
import java.math.BigDecimal;
import java.time.LocalDate;
import com.ubs.expensemanager.domain.Category;
import com.ubs.expensemanager.domain.enums.ExpenseStatus;

public record ExpenseResponse(UUID id, 
                            Category category, 
                            BigDecimal amount, 
                            String currency, 
                            LocalDate date, 
                            ExpenseStatus status,
                            String description) 
{}

    

