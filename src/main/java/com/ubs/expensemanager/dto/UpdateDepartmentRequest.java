package com.ubs.expensemanager.dto;

import java.math.BigDecimal;

public record UpdateDepartmentRequest(String name,
                                    BigDecimal monthlyBudget
                                    ) 
{}
