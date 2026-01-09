package com.ubs.expensemanager.dto.department;

import java.math.BigDecimal;

public record CreateDepartmentRequest(String name,
                                    BigDecimal monthlyBudget
                                    ) 
{}
