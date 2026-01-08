package com.ubs.expensemanager.dto;

import java.math.BigDecimal;
import java.util.UUID;


public record DepartmentResponse(UUID id,
                                String name,
                                BigDecimal monthlyBudget
                                ) 
{}
