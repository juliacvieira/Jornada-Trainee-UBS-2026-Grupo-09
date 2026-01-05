package com.ubs.expensemanager.controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ubs.expensemanager.service.ExpenseService;
import com.ubs.expensemanager.domain.Expense;
import com.ubs.expensemanager.dto.CreateExpenseRequest;
import com.ubs.expensemanager.dto.UpdateExpenseRequest;

@RestController
@RequestMapping("/expense")
public class ExpenseController {

    private final ExpenseService service;

    public ExpenseController(ExpenseService service){
        this.service = service;
    }

    @GetMapping
    public List<Expense> getExpenses(){
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Expense> getExpensesById(@PathVariable UUID id){
        return service.findById(id);
    }

    @PostMapping
    public Expense newExpense(@RequestBody CreateExpenseRequest request){
        return service.createExpense(request);
    }

    @PatchMapping("/{id}")
    public Expense updateExpense(@PathVariable UUID id, @RequestBody UpdateExpenseRequest request){
        return service.updateExpense(id, request);
    }
}
