package com.ubs.expensemanager.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.ubs.expensemanager.domain.Department;
import com.ubs.expensemanager.dto.DepartmentResponse;

@Component
public class DepartmentMapper {

    public DepartmentResponse toResponse (Department department){
        return new DepartmentResponse(
            department.getId(),
            department.getName(),
            department.getMonthlyBudget()
        );
    }

    public List<DepartmentResponse> toResponseList (List<Department> departments){
        return departments.stream()
                .map(department -> toResponse(department))
                .toList();
    }
}
