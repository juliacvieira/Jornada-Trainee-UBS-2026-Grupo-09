package com.ubs.expensemanager.mapper;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Component;

import com.ubs.expensemanager.domain.Department;
import com.ubs.expensemanager.dto.department.DepartmentResponse;

@Component
public class DepartmentMapper {

    public DepartmentResponse toResponse (Department department, BigDecimal monthlyBudgetUsed){
        return new DepartmentResponse(
    		department.getId(),
            department.getName(),
            department.getMonthlyBudget(),
            monthlyBudgetUsed
        );
    }

    public List<DepartmentResponse> toResponseList (List<Department> departments, BigDecimal monthlyBudgetUsed){
        return departments.stream()
                .map(department -> toResponse(department, monthlyBudgetUsed))
                .toList();
    }
}
