package com.ubs.expensemanager.service;

import com.ubs.expensemanager.domain.Category;
import com.ubs.expensemanager.domain.Department;
import com.ubs.expensemanager.domain.Employee;
import com.ubs.expensemanager.domain.Expense;
import com.ubs.expensemanager.domain.enums.AlertType;
import com.ubs.expensemanager.domain.enums.ExpenseStatus;
import com.ubs.expensemanager.dto.expense.CreateExpenseRequest;
import com.ubs.expensemanager.repository.CategoryRepository;
import com.ubs.expensemanager.repository.EmployeeRepository;
import com.ubs.expensemanager.repository.ExpenseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for ExpenseService (createExpense scenarios).
 * Adjusted to use UUID ids (matches your DTO/entity types).
 */
class ExpenseServiceTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private AlertService alertService;

    @Mock
    private ReceiptStorageService receiptStorageService;

    @InjectMocks
    private ExpenseService expenseService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @SuppressWarnings("null")
    void createExpense_whenExceedsDailyCategoryLimit_shouldMarkNeedsReviewAndCreateAlert() {
        // given
        UUID employeeId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();

        CreateExpenseRequest request = mock(CreateExpenseRequest.class);
        when(request.employeeId()).thenReturn(employeeId);
        when(request.categoryId()).thenReturn(categoryId);
        when(request.amount()).thenReturn(BigDecimal.valueOf(150));
        when(request.date()).thenReturn(LocalDate.now());
        when(request.currency()).thenReturn("BRL");
        when(request.description()).thenReturn("Almoço executivo");

        Department dept = new Department();
        dept.setId(UUID.randomUUID());
        dept.setName("Vendas");
        dept.setMonthlyBudget(BigDecimal.valueOf(5000));

        Employee employee = new Employee();
        employee.setId(employeeId);
        employee.setName("João");
        employee.setEmail("joao@ubs.com");
        employee.setDepartment(dept);

        Category category = new Category();
        category.setId(categoryId);
        category.setName("Refeição");
        category.setDailyLimit(BigDecimal.valueOf(100));
        category.setMonthlyLimit(BigDecimal.valueOf(1000));

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        // Mock department budget check (should not exceed)
        when(expenseRepository.sumAmountByDepartmentAndMonth(eq(dept.getId()), any(LocalDate.class),
                any(LocalDate.class), eq(ExpenseStatus.APPROVED_FINANCE)))
                .thenReturn(BigDecimal.valueOf(100));

        when(expenseRepository.save(any(Expense.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        Expense result = expenseService.createExpense(request);

        // then
        assertNotNull(result);
        assertEquals(ExpenseStatus.PENDING, result.getStatus());
        assertTrue(result.isNeedsReview(), "Expense above daily limit should be marked needsReview");

        verify(alertService, times(1)).createAlert(eq(result), eq(AlertType.CATEGORY_LIMIT), anyString());
    }

    @Test
    @SuppressWarnings("null")
    void createExpense_whenDepartmentBudgetExceeded_shouldCreateAlertAndMarkNeedsReview() {
        // given
        UUID employeeId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();

        CreateExpenseRequest request = mock(CreateExpenseRequest.class);
        when(request.employeeId()).thenReturn(employeeId);
        when(request.categoryId()).thenReturn(categoryId);
        when(request.amount()).thenReturn(BigDecimal.valueOf(900));
        when(request.date()).thenReturn(LocalDate.now());
        when(request.currency()).thenReturn("BRL");
        when(request.description()).thenReturn("Evento externo");

        Department dept = new Department();
        dept.setId(UUID.randomUUID());
        dept.setName("Comercial");
        dept.setMonthlyBudget(BigDecimal.valueOf(1000)); // small budget to trigger exceed

        Employee employee = new Employee();
        employee.setId(employeeId);
        employee.setName("Maria");
        employee.setDepartment(dept);

        Category category = new Category();
        category.setId(categoryId);
        category.setName("Viagem");
        category.setDailyLimit(null);
        category.setMonthlyLimit(null);

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        // sumAmountByDepartmentAndMonth(...) will be called inside
        // validateExpenseForCreate
        when(expenseRepository.sumAmountByDepartmentAndMonth(eq(dept.getId()), any(LocalDate.class),
                any(LocalDate.class), eq(ExpenseStatus.APPROVED_FINANCE)))
                .thenReturn(BigDecimal.valueOf(200));

        when(expenseRepository.save(any(Expense.class))).thenAnswer(invocation -> {
            Expense e = invocation.getArgument(0);
            if (e.getId() == null)
                e.setId(UUID.randomUUID());
            return e;
        });

        // when
        Expense result = expenseService.createExpense(request);

        // then
        assertNotNull(result);
        assertEquals(ExpenseStatus.PENDING, result.getStatus());
        assertTrue(result.isNeedsReview(), "Expense exceeding department budget should be marked needsReview");

        // alert should be created
        verify(alertService, times(1)).createAlert(any(Expense.class), eq(AlertType.DEPARTMENT_BUDGET), anyString());
    }
}
