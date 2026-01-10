package com.ubs.expensemanager.dto.category;

import java.math.BigDecimal;
import java.util.UUID;


public record CategoryResponse(
		    UUID id,
		    String name,
		    BigDecimal dailyLimit,
		    BigDecimal monthlyLimit
	) {}
