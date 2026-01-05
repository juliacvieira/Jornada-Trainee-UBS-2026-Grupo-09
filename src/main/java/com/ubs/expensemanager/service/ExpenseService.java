package com.ubs.expensemanager.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ubs.expensemanager.domain.Expense;
import com.ubs.expensemanager.domain.enums.ExpenseStatus;
import com.ubs.expensemanager.repository.ExpenseRepository;

@Service
public class ExpenseService {

    private final ExpenseRepository repository;

    public ExpenseService(ExpenseRepository repository){
        this.repository = repository;
    }

    public Expense createExpense (Expense expense){
        validateExpense(expense);
        return expense;
    }

    public List<Expense> findAll(){
        return repository.findAll();
    }

    private void validateExpense (Expense expense){
        //validacao - work in progress

        expense.setStatus(ExpenseStatus.PENDING);
    }
}
