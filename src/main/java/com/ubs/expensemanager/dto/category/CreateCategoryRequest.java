package com.ubs.expensemanager.dto.category;

import java.math.BigDecimal;


public record CreateCategoryRequest(
		String name,
        BigDecimal dailyLimit,
        BigDecimal monthlyLimit) 
{}
