package com.ubs.expensemanager.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ubs.expensemanager.domain.Expense;

@Service
public class ExpenseService {

    public List<Expense> allExpenses (){
        return List.of();
    }

    public Expense createExpense (Expense expense){
        return expense;
    }

}
