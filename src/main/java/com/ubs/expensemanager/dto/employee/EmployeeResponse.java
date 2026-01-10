package com.ubs.expensemanager.dto.employee;

import java.util.UUID;

import com.ubs.expensemanager.domain.Department;
import com.ubs.expensemanager.domain.Employee;

public record EmployeeResponse(
		    UUID id,
		    String name,
		    String email,
		    String position,
		    UUID departmentId,
		    UUID managerId
	) {}
