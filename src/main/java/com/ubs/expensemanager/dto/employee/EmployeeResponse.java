package com.ubs.expensemanager.dto.employee;

import java.util.UUID;

public record EmployeeResponse(
		    UUID id,
		    String name,
		    String email,
		    String position,
		    UUID departmentId,
		    UUID managerId
	) {}
