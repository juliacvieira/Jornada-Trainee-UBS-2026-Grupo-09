package com.ubs.expensemanager.dto.employee;


public record CreateEmployeeRequest(String name,
                                    String email,
                                    String manager,
                                    String department,
                                    String position
                                    ) 
{}
