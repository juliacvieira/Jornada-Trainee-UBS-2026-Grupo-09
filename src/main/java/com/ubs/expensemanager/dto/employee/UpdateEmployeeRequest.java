package com.ubs.expensemanager.dto.employee;

import java.util.UUID;

public record UpdateEmployeeRequest(
		String name,
		String email,
		String position,
		UUID departmentId,
		UUID managerId)
{}
