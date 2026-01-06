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
import com.ubs.expensemanager.dto.ExpenseResponse;
import com.ubs.expensemanager.dto.UpdateExpenseRequest;
import com.ubs.expensemanager.mapper.ExpenseMapper;

@RestController
@RequestMapping("/expense")
public class ExpenseController {

    private final ExpenseService service;
    private final ExpenseMapper mapper;

    public ExpenseController(ExpenseService service, ExpenseMapper mapper){
        this.service = service;
        this.mapper = mapper;
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
    public ExpenseResponse newExpense(@RequestBody CreateExpenseRequest request){
        Expense expense = service.createExpense(request);
        return mapper.toResponse(expense);
    }

    @PatchMapping("/{id}")
    public Expense updateExpense(@PathVariable UUID id, @RequestBody UpdateExpenseRequest request){
        return service.updateExpense(id, request);
    }
}
