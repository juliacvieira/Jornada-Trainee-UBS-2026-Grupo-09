package com.ubs.expensemanager.mapper;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.ubs.expensemanager.domain.Department;
import com.ubs.expensemanager.domain.Employee;
import com.ubs.expensemanager.dto.employee.EmployeeResponse;

@Component
public class EmployeeMapper {
    
    public EmployeeResponse toResponse (Employee employee){
    	
    	Department dept = employee.getDepartment();
        Employee manager = employee.getManager();

        UUID deptId = dept != null ? dept.getId() : null;
        UUID managerId = manager != null ? manager.getId() : null;
    	
        return new EmployeeResponse(
    		employee.getId(),
            employee.getName(),
            employee.getEmail(),
            employee.getPosition(),
            deptId,
            managerId
        );
    }

    public List<EmployeeResponse> toResponseList (List<Employee> employees){
        return employees.stream()
                .map(employee -> toResponse(employee))
                .toList();
    }
}
