package com.ubs.expensemanager.mapper;

import java.util.List;

import org.springframework.stereotype.Controller;

import com.ubs.expensemanager.domain.Employee;
import com.ubs.expensemanager.dto.EmployeeResponse;

@Controller
public class EmployeeMapper {
public EmployeeResponse toResponse (Employee employee){
        return new EmployeeResponse(
            employee.getId(),
            employee.getName(),
            employee.getEmail(),
            employee.getManager(),
            employee.getDepartment(),
            employee.getPosition()
        );
    }

    public List<EmployeeResponse> toResponseList (List<Employee> employees){
        return employees.stream()
                .map(employee -> toResponse(employee))
                .toList();
    }
}
