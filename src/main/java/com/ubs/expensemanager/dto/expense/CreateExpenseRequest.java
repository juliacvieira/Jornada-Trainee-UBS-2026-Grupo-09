package com.ubs.expensemanager.dto.expense;

import java.math.BigDecimal;
import java.time.LocalDate;


public record CreateExpenseRequest(String employee,
                                    String category,
                                    BigDecimal amount,
                                    String currency,
                                    LocalDate date,
                                    String description) 
{}