package com.ubs.expensemanager.dto.employee;

import java.util.UUID;

import com.ubs.expensemanager.domain.enums.EmployeeRole;

public record UpdateEmployeeRequest(
		String name,
		String email,
		String position,
		UUID departmentId,
		UUID managerId,
		EmployeeRole role)
{}
