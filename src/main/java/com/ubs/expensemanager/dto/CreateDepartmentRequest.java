package com.ubs.expensemanager.dto;

import java.math.BigDecimal;

public record CreateDepartmentRequest(String name,
                                    BigDecimal monthlyBudget
                                    ) 
{}
