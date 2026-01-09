package com.ubs.expensemanager.dto.category;

import java.math.BigDecimal;


public record UpdateCategoryRequest(String name,
                                    BigDecimal dailyLimit,
                                    BigDecimal monthlyLimit
                                    ) 
{}
