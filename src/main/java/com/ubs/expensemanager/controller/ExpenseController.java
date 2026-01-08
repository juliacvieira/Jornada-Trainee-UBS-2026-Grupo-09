package com.ubs.expensemanager.controller;

import java.util.List;
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
    public List<ExpenseResponse> getExpenses(){
        List<Expense> expenses = service.findAll();
        return mapper.toResponseList(expenses);
    }

    @GetMapping("/{id}")
    public ExpenseResponse getExpensesById(@PathVariable UUID id){
        Expense expense =  service.findById(id);
        return mapper.toResponse(expense);
    }

    @PostMapping
    public ExpenseResponse newExpense(@RequestBody CreateExpenseRequest request){
        Expense expense = service.createExpense(request);
        return mapper.toResponse(expense);
    }

    @PatchMapping("/{id}")
    public ExpenseResponse updateExpense(@PathVariable UUID id, @RequestBody UpdateExpenseRequest request){
        Expense expense = service.updateExpense(id, request);
        return mapper.toResponse(expense);
    }
}
