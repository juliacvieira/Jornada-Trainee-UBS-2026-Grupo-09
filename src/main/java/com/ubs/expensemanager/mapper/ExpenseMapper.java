package com.ubs.expensemanager.mapper;

import org.springframework.stereotype.Component;

import com.ubs.expensemanager.domain.Expense;
import com.ubs.expensemanager.dto.ExpenseResponse;

@Component
public class ExpenseMapper {

    public ExpenseResponse toResponse (Expense expense){
        return new ExpenseResponse(
            expense.getId(),
            expense.getCategory(),
            expense.getAmount(),
            expense.getCurrency(),
            expense.getDate(),
            expense.getStatus()
        );
    }

}
