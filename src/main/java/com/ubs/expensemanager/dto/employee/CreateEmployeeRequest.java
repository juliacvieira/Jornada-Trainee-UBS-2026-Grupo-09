package com.ubs.expensemanager.dto.employee;

import com.ubs.expensemanager.domain.Department;
import com.ubs.expensemanager.domain.Employee;

public record CreateEmployeeRequest(String name,
                                    String email,
                                    Employee manager,
                                    Department department,
                                    String position
                                    ) 
{}
