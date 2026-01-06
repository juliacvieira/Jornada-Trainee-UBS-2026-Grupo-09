package com.ubs.expensemanager.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.ubs.expensemanager.domain.Category;
import com.ubs.expensemanager.domain.Employee;

public record CreateExpenseRequest(Employee employee,
                                    Category category,
                                    BigDecimal amount,
                                    String currency,
                                    LocalDate date) 
{}