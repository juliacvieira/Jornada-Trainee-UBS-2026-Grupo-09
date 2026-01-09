package com.ubs.expensemanager.dto.department;

import java.math.BigDecimal;

public record UpdateDepartmentRequest(String name,
                                    BigDecimal monthlyBudget
                                    ) 
{}
