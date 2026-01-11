package com.ubs.expensemanager.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ubs.expensemanager.domain.Category;
import com.ubs.expensemanager.domain.Department;
import com.ubs.expensemanager.domain.Employee;
import com.ubs.expensemanager.domain.Expense;
import com.ubs.expensemanager.domain.enums.ExpenseStatus;
import com.ubs.expensemanager.dto.expense.CreateExpenseRequest;
import com.ubs.expensemanager.dto.expense.UpdateExpenseRequest;
import com.ubs.expensemanager.exception.BusinessException;
import com.ubs.expensemanager.repository.CategoryRepository;
import com.ubs.expensemanager.repository.EmployeeRepository;
import com.ubs.expensemanager.repository.ExpenseRepository;
import com.ubs.expensemanager.service.AlertService;

@Service
public class ExpenseService {

	private final ExpenseRepository expenseRepository;
    private final EmployeeRepository employeeRepository;
    private final CategoryRepository categoryRepository;
    private final AlertService alertService;

    public ExpenseService(ExpenseRepository expenseRepository,
            EmployeeRepository employeeRepository,
            CategoryRepository categoryRepository,
            AlertService alertService) {
		this.expenseRepository = expenseRepository;
		this.employeeRepository = employeeRepository;
		this.categoryRepository = categoryRepository;
	}

    @Transactional
    public Expense createExpense(CreateExpenseRequest request) {
        Employee employee = employeeRepository.findById(request.employeeId())
                .orElseThrow(() -> new BusinessException("Employee not found"));
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new BusinessException("Category not found"));

        Expense expense = new Expense();
        expense.setEmployee(employee);
        expense.setCategory(category);
        expense.setAmount(request.amount());
        expense.setDate(request.date() != null ? request.date() : LocalDate.now());
        expense.setCurrency((request.currency() == null || request.currency().isBlank()) ? "BRL" : request.currency());
        expense.setDescription(request.description());
        expense.setStatus(ExpenseStatus.PENDING);

        Expense saved = expenseRepository.save(expense);

        validateExpenseForCreate(saved);

        return expenseRepository.save(saved);
    }

    @Transactional
    public Expense updateExpense(UUID id, UpdateExpenseRequest request) {
        Expense existing = findById(id);

        Category newCategory = categoryRepository.findById(request.categoryId())
            .orElseThrow(() -> new BusinessException("Category not found"));

        Expense candidate = new Expense();
        candidate.setId(existing.getId());
        candidate.setEmployee(existing.getEmployee());
        candidate.setCategory(newCategory);
        candidate.setAmount(request.amount());
        candidate.setDate(request.date());
        candidate.setCurrency(request.currency());
        candidate.setDescription(request.description());
        candidate.setStatus(existing.getStatus());

        validateExpenseForUpdate(existing, candidate);

        existing.setCategory(newCategory);
        existing.setAmount(request.amount());
        existing.setDate(request.date());
        existing.setCurrency(request.currency());
        existing.setDescription(request.description());

        return expenseRepository.save(existing);
    }       
    
	public List<Expense> findAll() {
	    return expenseRepository.findAll();
	}
	
	public Expense findById(UUID id) {
	    return expenseRepository.findById(id)
	        .orElseThrow(() -> new BusinessException("Expense " + id + " not found"));
	}

    private void validateExpenseForCreate(Expense expense) {
        BigDecimal amount = expense.getAmount();
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Amount must be positive");
        }

        Category category = expense.getCategory();
        if (category == null) throw new BusinessException("Category not set");

        // check limits per category
        if (category.getDailyLimit() != null && amount.compareTo(category.getDailyLimit()) > 0) {
            expense.setNeedsReview(true);
            alertService.createAlert(expense, com.ubs.expensemanager.domain.enums.AlertType.CATEGORY_LIMIT,
                    "Daily category limit exceeded");
        }
        if (category.getMonthlyLimit() != null && amount.compareTo(category.getMonthlyLimit()) > 0) {
            expense.setNeedsReview(true);
            alertService.createAlert(expense, com.ubs.expensemanager.domain.enums.AlertType.CATEGORY_LIMIT,
                    "Monthly category limit exceeded");
        }

        // department budget considering the the already approved expenses
        Employee employee = expense.getEmployee();
        if (alreadySpent.add(amount).compareTo(department.getMonthlyBudget()) > 0) {
            expense.setNeedsReview(true);
            alertService.createAlert(expense, com.ubs.expensemanager.domain.enums.AlertType.DEPARTMENT_BUDGET,
                    "Department monthly budget exceeded");
        }

        YearMonth ym = YearMonth.from(expense.getDate());
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();

        BigDecimal alreadySpent = expenseRepository.sumAmountByDepartmentAndMonth(
            department.getId(), start, end, ExpenseStatus.APPROVED_FINANCE);

        if (alreadySpent == null) alreadySpent = BigDecimal.ZERO;

        if (alreadySpent.add(amount).compareTo(department.getMonthlyBudget()) > 0) {
            throw new BusinessException("Department monthly budget exceeded");
        }
    }

    private void validateExpenseForUpdate(Expense existing, Expense candidate) {
        BigDecimal amount = candidate.getAmount();
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Amount must be positive");
        }

        Category category = candidate.getCategory();
        if (category == null) throw new BusinessException("Category not set");

        if (category.getDailyLimit() != null && amount.compareTo(category.getDailyLimit()) > 0) {
            throw new BusinessException("Daily category limit exceeded");
        }
        if (category.getMonthlyLimit() != null && amount.compareTo(category.getMonthlyLimit()) > 0) {
            throw new BusinessException("Monthly category limit exceeded");
        }

        Department department = existing.getEmployee().getDepartment();
        if (department == null || department.getMonthlyBudget() == null) {
            throw new BusinessException("Department budget not configured");
        }

        YearMonth ym = YearMonth.from(candidate.getDate());
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();

        // sum of month *excluding* the expense that will be update
        BigDecimal alreadySpentExcluding = expenseRepository.sumAmountByDepartmentAndMonthExcluding(
            department.getId(), start, end, ExpenseStatus.APPROVED_FINANCE, existing.getId());

        if (alreadySpentExcluding == null) alreadySpentExcluding = BigDecimal.ZERO;

        if (alreadySpentExcluding.add(amount).compareTo(department.getMonthlyBudget()) > 0) {
            throw new BusinessException("Department monthly budget exceeded");
        }
    }

}
