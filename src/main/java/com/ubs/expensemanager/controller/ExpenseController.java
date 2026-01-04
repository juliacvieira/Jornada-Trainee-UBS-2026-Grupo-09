package com.ubs.expensemanager.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ubs.expensemanager.service.ExpenseService;
import com.ubs.expensemanager.domain.Expense;

@RestController
@RequestMapping("/expense")
public class ExpenseController {

    private final ExpenseService service;

    public ExpenseController(ExpenseService service){
        this.service = service;
    }

    @GetMapping
    public List<Expense> getExpenses(){
        return service.allExpenses();
    }

    @GetMapping("/expense/{id}")
    public String getExpensesById(@PathVariable Long id){
        return "teste - id: " + id;
    }

    @PostMapping
    public Expense newExpense(@RequestBody Expense expense){
        return service.createExpense(expense);
    }
}
