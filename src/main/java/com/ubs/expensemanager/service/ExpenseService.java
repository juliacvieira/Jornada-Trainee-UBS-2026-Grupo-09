package com.ubs.expensemanager.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.ubs.expensemanager.domain.Category;
import com.ubs.expensemanager.domain.Department;
import com.ubs.expensemanager.domain.Employee;
import com.ubs.expensemanager.domain.Expense;
import com.ubs.expensemanager.domain.enums.ExpenseStatus;
import com.ubs.expensemanager.dto.expense.CreateExpenseRequest;
import com.ubs.expensemanager.dto.expense.UpdateExpenseRequest;
import com.ubs.expensemanager.repository.CategoryRepository;
import com.ubs.expensemanager.repository.DepartmentRepository;
import com.ubs.expensemanager.repository.EmployeeRepository;
import com.ubs.expensemanager.repository.ExpenseRepository;

@Service
public class ExpenseService {

    private final ExpenseRepository repository;
    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final CategoryRepository categoryRepository;

    public ExpenseService(ExpenseRepository repository, 
                        DepartmentRepository departmentRepository, 
                        CategoryRepository categoryRepository,
                        EmployeeRepository employeeRepository){
        this.repository = repository;
        this.departmentRepository = departmentRepository;
        this.categoryRepository = categoryRepository;
        this.employeeRepository = employeeRepository;
    }

    public Expense createExpense (CreateExpenseRequest request){
        Expense expense = new Expense();

        String name = request.employee();
        String categoryName = request.category();
        Employee employee = employeeRepository.findByName(name).orElseThrow(() -> new NoSuchElementException("Employee " + name + " not found"));
        Category category = categoryRepository.findByName(categoryName).orElseThrow(() -> new NoSuchElementException("Employee " + categoryName + " not found"));
        
        
        /*Department department = employee.getDepartment();*/
        


        expense.setAmount(request.amount());
        expense.setCategory(category);
        expense.setDate(request.date());
        expense.setCurrency(request.currency());
        expense.setEmployee(employee);
        expense.setDescription(request.description());
        expense.setStatus(ExpenseStatus.PENDING);


        validateExpense(expense);

        Expense saved = repository.save(expense);
        return saved;
    }

    public Expense updateExpense (UUID id, UpdateExpenseRequest request){
        Expense expense = findById(id);

        expense.setAmount(request.amount());
        expense.setCategory(request.category());
        expense.setCurrency(request.currency());
        expense.setDate(request.date());
        expense.setDescription(request.description());

        return expense;        
    }

    public List<Expense> findAll(){
        return repository.findAll();
    }

    public Expense findById(UUID id){
        return repository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("Expense" + id + "not found"));
    }

    private void validateExpense (Expense expense){
        //validacao - work in progress
        Employee employee = expense.getEmployee();
        Department department = employee.getDepartment();
        Category category = expense.getCategory();
        
        BigDecimal budget = department.getMonthlyBudget();
        BigDecimal dailyLimit = category.getDailyLimit();
        BigDecimal monthlyLimit = category.getMonthlyLimit();
        BigDecimal amount = expense.getAmount();

        if (amount.compareTo(budget) < 0){
            throw new RuntimeException ("Limit surpassed");
        }
        if (amount.compareTo(dailyLimit) < 0){
            throw new RuntimeException ("Limit surpassed");
        }
        if (amount.compareTo(monthlyLimit) < 0){
            throw new RuntimeException ("Limit surpassed");
        }
    }
}
