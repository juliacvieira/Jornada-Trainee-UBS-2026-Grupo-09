package com.ubs.expensemanager.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.ubs.expensemanager.domain.Expense;
import com.ubs.expensemanager.dto.expense.ExpenseResponse;

@Component
public class ExpenseMapper {

	 public static ExpenseResponse toResponse(Expense expense) {
        return new ExpenseResponse(
            expense.getId(),
            expense.getEmployee() != null ? expense.getEmployee().getId() : null,
            expense.getEmployee() != null ? expense.getEmployee().getName() : null,
            expense.getCategory() != null ? expense.getCategory().getId() : null,
            expense.getCategory() != null ? expense.getCategory().getName() : null,
            expense.getAmount(),
            expense.getCurrency(),
            expense.getDate(),
            expense.getDescription(),
            expense.getStatus(),
            expense.getReceiptUrl(),
            expense.getReceiptFilename()
        );
    }

    public List<ExpenseResponse> toResponseList (List<Expense> expenses){
        return expenses.stream()
                .map(expense -> toResponse(expense))
                .toList();
    }

}
