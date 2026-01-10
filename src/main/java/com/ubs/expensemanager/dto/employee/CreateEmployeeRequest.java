package com.ubs.expensemanager.dto.employee;

import java.util.UUID;

public record CreateEmployeeRequest(
		String name,
		String email,
		String position,
		UUID departmentId,
		UUID managerId)
{}
