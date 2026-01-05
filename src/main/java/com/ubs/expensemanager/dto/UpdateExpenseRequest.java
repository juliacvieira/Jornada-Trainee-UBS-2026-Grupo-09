package com.ubs.expensemanager.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.ubs.expensemanager.domain.Category;
import com.ubs.expensemanager.domain.Employee;

public class UpdateExpenseRequest {
    private Employee employee;
    private Category category;
    private BigDecimal amount;
    private String currency;
    private LocalDate date;
    
    public Employee getEmployee() {
        return employee;
    }
    public void setEmployee(Employee employee) {
        this.employee = employee;
    }
    public Category getCategory() {
        return category;
    }
    public void setCategory(Category category) {
        this.category = category;
    }
    public BigDecimal getAmount() {
        return amount;
    }
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    public String getCurrency() {
        return currency;
    }
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }

    
}
