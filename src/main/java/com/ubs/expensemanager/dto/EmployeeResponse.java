package com.ubs.expensemanager.dto;

import java.util.UUID;

import com.ubs.expensemanager.domain.Department;
import com.ubs.expensemanager.domain.Employee;

public record EmployeeResponse(UUID id,
                                String name,
                                String email,
                                Employee manager,
                                Department department,
                                String position
                                ) 
{}
