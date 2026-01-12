package com.ubs.expensemanager.service;

import com.ubs.expensemanager.domain.*;
import com.ubs.expensemanager.domain.enums.ExpenseStatus;
import com.ubs.expensemanager.dto.expense.UpdateExpenseRequest;
import com.ubs.expensemanager.exception.BusinessException;
import com.ubs.expensemanager.repository.CategoryRepository;
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

class ExpenseUpdateServiceTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ExpenseService expenseService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void updateExpense_whenExcludingMonthSumExceedsBudget_shouldThrow() {
        // existing expense (that will be excluded from the month sum)
        UUID expenseId = UUID.randomUUID();
        Expense existing = new Expense();
        existing.setId(expenseId);
        Department dept = new Department();
        dept.setId(UUID.randomUUID());
        dept.setMonthlyBudget(BigDecimal.valueOf(1000));
        Employee emp = new Employee();
        emp.setId(UUID.randomUUID());
        emp.setDepartment(dept);
        existing.setEmployee(emp);
        existing.setDate(LocalDate.now());
        existing.setStatus(ExpenseStatus.PENDING);

        when(expenseRepository.findById(expenseId)).thenReturn(Optional.of(existing));

        // new category
        Category category = new Category();
        category.setId(UUID.randomUUID());
        category.setName("Transporte");
        category.setDailyLimit(null);
        category.setMonthlyLimit(null);

        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));

        // prepare request mock
        UpdateExpenseRequest req = mock(UpdateExpenseRequest.class);
        when(req.categoryId()).thenReturn(category.getId());
        when(req.amount()).thenReturn(BigDecimal.valueOf(900)); // new amount
        when(req.date()).thenReturn(LocalDate.now());
        when(req.currency()).thenReturn("BRL");
        when(req.description()).thenReturn("Táxi");

        // sum excluding this expense: alreadySpentExcluding = 200 => 200 + 900 = 1100 > 1000 => should throw
        when(expenseRepository.sumAmountByDepartmentAndMonthExcluding(eq(dept.getId()), any(LocalDate.class), any(LocalDate.class), eq(ExpenseStatus.APPROVED_FINANCE), eq(existing.getId())))
                .thenReturn(BigDecimal.valueOf(200));

        BusinessException ex = assertThrows(BusinessException.class, () -> expenseService.updateExpense(expenseId, req));
        assertTrue(ex.getMessage().toLowerCase().contains("budget"));
    }

    @Test
    void updateExpense_whenWithinBudget_shouldSaveAndReturnUpdated() {
        UUID expenseId = UUID.randomUUID();
        Expense existing = new Expense();
        existing.setId(expenseId);
        Department dept = new Department();
        dept.setId(UUID.randomUUID());
        dept.setMonthlyBudget(BigDecimal.valueOf(5000));
        Employee emp = new Employee();
        emp.setId(UUID.randomUUID());
        emp.setDepartment(dept);
        existing.setEmployee(emp);
        existing.setDate(LocalDate.now());
        existing.setStatus(ExpenseStatus.PENDING);

        when(expenseRepository.findById(expenseId)).thenReturn(Optional.of(existing));

        Category category = new Category();
        category.setId(UUID.randomUUID());
        category.setName("Outros");
        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));

        UpdateExpenseRequest req = mock(UpdateExpenseRequest.class);
        when(req.categoryId()).thenReturn(category.getId());
        when(req.amount()).thenReturn(BigDecimal.valueOf(400));
        when(req.date()).thenReturn(LocalDate.now());
        when(req.currency()).thenReturn("BRL");
        when(req.description()).thenReturn("Material");

        when(expenseRepository.sumAmountByDepartmentAndMonthExcluding(eq(dept.getId()), any(LocalDate.class), any(LocalDate.class), eq(ExpenseStatus.APPROVED_FINANCE), eq(existing.getId())))
                .thenReturn(BigDecimal.valueOf(1000)); // 1000 + 400 = 1400 <= 5000

        when(expenseRepository.save(any(Expense.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Expense updated = expenseService.updateExpense(expenseId, req);

        assertNotNull(updated);
        assertEquals(req.amount(), updated.getAmount());
        assertEquals(req.description(), updated.getDescription());
        verify(expenseRepository, times(1)).save(existing);
    }
}
