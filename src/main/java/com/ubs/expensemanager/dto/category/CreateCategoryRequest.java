package com.ubs.expensemanager.dto;

import java.math.BigDecimal;


public record CreateCategoryRequest(String name,
                                    BigDecimal dailyLimit,
                                    BigDecimal monthlyLimit
                                    ) 
{}
