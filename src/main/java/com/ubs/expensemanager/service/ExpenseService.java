package com.ubs.expensemanager.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.ubs.expensemanager.domain.Expense;
import com.ubs.expensemanager.domain.enums.ExpenseStatus;
import com.ubs.expensemanager.dto.CreateExpenseRequest;
import com.ubs.expensemanager.dto.ExpenseResponse;
import com.ubs.expensemanager.dto.UpdateExpenseRequest;
import com.ubs.expensemanager.handler.ControllerExceptionHandler;
import com.ubs.expensemanager.repository.ExpenseRepository;

@Service
public class ExpenseService {

    private final ExpenseRepository repository;

    public ExpenseService(ExpenseRepository repository){
        this.repository = repository;
    }

    public Expense createExpense (CreateExpenseRequest request){
        Expense expense = new Expense();

        expense.setAmount(request.amount());
        expense.setCategory(request.category());
        expense.setDate(request.date());
        expense.setCurrency(request.currency());
        expense.setEmployee(request.employee());

        boolean valid = validateExpense(expense);

        try {
            if (valid == true) {
            Expense saved = repository.save(expense);
            return saved;
        }
        } catch (Exception e) {
            System.out.println("Validation exception: " + e);
        }

        return expense;

    }

    public Expense updateExpense (UUID id, UpdateExpenseRequest request){
        expense = findById(id);

        
    }

    public List<Expense> findAll(){
        return repository.findAll();
    }

    public Expense findById(UUID id){
        return repository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("Expense" + id + "not found"));
    }

    private boolean validateExpense (Expense expense){
        //validacao - work in progress

        expense.setStatus(ExpenseStatus.PENDING);

        return true;
    }
}
