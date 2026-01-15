package com.ubs.expensemanager.service;

import com.ubs.expensemanager.domain.*;
import com.ubs.expensemanager.domain.enums.AlertType;
import com.ubs.expensemanager.domain.enums.ExpenseStatus;
import com.ubs.expensemanager.dto.expense.CreateExpenseRequest;
import com.ubs.expensemanager.dto.expense.UpdateExpenseRequest;
import com.ubs.expensemanager.exception.BusinessException;
import com.ubs.expensemanager.repository.CategoryRepository;
import com.ubs.expensemanager.repository.EmployeeRepository;
import com.ubs.expensemanager.repository.ExpenseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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

    // Helpers
    private Employee makeEmployeeWithDept(UUID deptId, BigDecimal monthlyBudget) {
        Department dept = new Department();
        dept.setId(deptId);
        dept.setMonthlyBudget(monthlyBudget);

        Employee emp = new Employee();
        emp.setId(UUID.randomUUID());
        emp.setDepartment(dept);
        return emp;
    }

    private Category makeCategory(BigDecimal dailyLimit, BigDecimal monthlyLimit) {
        Category cat = new Category();
        cat.setId(UUID.randomUUID());
        cat.setDailyLimit(dailyLimit);
        cat.setMonthlyLimit(monthlyLimit);
        return cat;
    }

    @Test
    void createExpense_whenEmployeeMissing_shouldThrow() {
        UUID empId = UUID.randomUUID();
        CreateExpenseRequest req = mock(CreateExpenseRequest.class);
        when(req.employeeId()).thenReturn(empId);

        when(employeeRepository.findById(empId)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class, () -> expenseService.createExpense(req));
        assertTrue(ex.getMessage().toLowerCase().contains("employee"));
        verify(expenseRepository, never()).save(any());
    }

    @Test
    void createExpense_whenCategoryMissing_shouldThrow() {
        UUID empId = UUID.randomUUID();
        UUID catId = UUID.randomUUID();
        CreateExpenseRequest req = mock(CreateExpenseRequest.class);
        when(req.employeeId()).thenReturn(empId);
        when(req.categoryId()).thenReturn(catId);

        when(employeeRepository.findById(empId)).thenReturn(Optional.of(new Employee()));
        when(categoryRepository.findById(catId)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class, () -> expenseService.createExpense(req));
        assertTrue(ex.getMessage().toLowerCase().contains("category"));
        verify(expenseRepository, never()).save(any());
    }

    @Test
    void createExpense_whenValid_shouldSaveAndReturnAndMaybeAlert() {
        UUID empId = UUID.randomUUID();
        UUID catId = UUID.randomUUID();

        Employee emp = makeEmployeeWithDept(UUID.randomUUID(), BigDecimal.valueOf(1000));
        Category cat = makeCategory(null, null); // no limits

        CreateExpenseRequest req = mock(CreateExpenseRequest.class);
        when(req.employeeId()).thenReturn(empId);
        when(req.categoryId()).thenReturn(catId);
        when(req.amount()).thenReturn(BigDecimal.valueOf(100));
        when(req.date()).thenReturn(LocalDate.now());
        when(req.currency()).thenReturn("USD");
        when(req.description()).thenReturn("Taxi");

        when(employeeRepository.findById(empId)).thenReturn(Optional.of(emp));
        when(categoryRepository.findById(catId)).thenReturn(Optional.of(cat));

        when(expenseRepository.save(any(Expense.class))).thenAnswer(inv -> {
            Expense e = inv.getArgument(0);
            if (e.getId() == null) e.setId(UUID.randomUUID());
            return e;
        });

        // Simulate department already spent = 0
        when(expenseRepository.sumAmountByDepartmentAndMonth(any(), any(), any(), any()))
                .thenReturn(BigDecimal.ZERO);

        Expense saved = expenseService.createExpense(req);

        assertNotNull(saved.getId());
        assertEquals(BigDecimal.valueOf(100), saved.getAmount());
        assertEquals("Taxi", saved.getDescription());
        // alertService should NOT be called because no limits set
        verify(alertService, never()).createAlert(any(), any(AlertType.class), anyString());
    }

    @Test
    void createExpense_whenAmountNullOrNegative_shouldThrow() {
        UUID empId = UUID.randomUUID();
        UUID catId = UUID.randomUUID();

        Employee emp = makeEmployeeWithDept(UUID.randomUUID(), BigDecimal.valueOf(1000));
        Category cat = makeCategory(null, null);

        CreateExpenseRequest req = mock(CreateExpenseRequest.class);
        when(req.employeeId()).thenReturn(empId);
        when(req.categoryId()).thenReturn(catId);
        when(req.amount()).thenReturn(null);

        when(employeeRepository.findById(empId)).thenReturn(Optional.of(emp));
        when(categoryRepository.findById(catId)).thenReturn(Optional.of(cat));

        when(expenseRepository.save(any(Expense.class))).thenAnswer(inv -> {
            Expense e = inv.getArgument(0);
            if (e.getId() == null) e.setId(UUID.randomUUID());
            return e;
        });

        BusinessException ex = assertThrows(BusinessException.class, () -> expenseService.createExpense(req));
        assertTrue(ex.getMessage().toLowerCase().contains("amount"));
    }

    @Test
    void createExpense_whenCategoryDailyLimitExceeded_shouldMarkNeedsReviewAndCreateAlert() {
        UUID empId = UUID.randomUUID();
        UUID catId = UUID.randomUUID();
        BigDecimal dailyLimit = BigDecimal.valueOf(50);
        Category cat = makeCategory(dailyLimit, null);
        Employee emp = makeEmployeeWithDept(UUID.randomUUID(), BigDecimal.valueOf(1000));

        CreateExpenseRequest req = mock(CreateExpenseRequest.class);
        when(req.employeeId()).thenReturn(empId);
        when(req.categoryId()).thenReturn(catId);
        when(req.amount()).thenReturn(BigDecimal.valueOf(100)); // exceeds daily limit
        when(req.date()).thenReturn(LocalDate.now());

        when(employeeRepository.findById(empId)).thenReturn(Optional.of(emp));
        when(categoryRepository.findById(catId)).thenReturn(Optional.of(cat));

        when(expenseRepository.save(any(Expense.class))).thenAnswer(inv -> {
            Expense e = inv.getArgument(0);
            if (e.getId() == null) e.setId(UUID.randomUUID());
            return e;
        });

        when(expenseRepository.sumAmountByDepartmentAndMonth(any(), any(), any(), any()))
                .thenReturn(BigDecimal.ZERO);

        Expense saved = expenseService.createExpense(req);

        assertTrue(saved.isNeedsReview());
        verify(alertService, times(1)).createAlert(eq(saved), any(AlertType.class), contains("limit"));
    }

    @Test
    void createExpense_whenDepartmentBudgetExceeded_shouldMarkNeedsReviewAndCreateAlert() {
        UUID empId = UUID.randomUUID();
        UUID catId = UUID.randomUUID();
        // category without limits
        Category cat = makeCategory(null, null);
        // dept monthly budget small so it will be exceeded
        Department dept = new Department();
        dept.setId(UUID.randomUUID());
        dept.setMonthlyBudget(BigDecimal.valueOf(100));
        Employee emp = new Employee();
        emp.setId(UUID.randomUUID());
        emp.setDepartment(dept);

        CreateExpenseRequest req = mock(CreateExpenseRequest.class);
        when(req.employeeId()).thenReturn(empId);
        when(req.categoryId()).thenReturn(catId);
        when(req.amount()).thenReturn(BigDecimal.valueOf(90));
        when(req.date()).thenReturn(LocalDate.now());

        when(employeeRepository.findById(empId)).thenReturn(Optional.of(emp));
        when(categoryRepository.findById(catId)).thenReturn(Optional.of(cat));

        // alreadySpent = 50 -> 50 + 90 = 140 > 100
        when(expenseRepository.sumAmountByDepartmentAndMonth(eq(dept.getId()), any(), any(), any()))
                .thenReturn(BigDecimal.valueOf(50));

        when(expenseRepository.save(any(Expense.class))).thenAnswer(inv -> {
            Expense e = inv.getArgument(0);
            if (e.getId() == null) e.setId(UUID.randomUUID());
            return e;
        });

        Expense saved = expenseService.createExpense(req);

        assertTrue(saved.isNeedsReview());
        verify(alertService, times(1)).createAlert(eq(saved), any(AlertType.class), contains("budget"));
    }

    @Test
    void updateExpense_whenExpenseNotFound_shouldThrow() {
        UUID id = UUID.randomUUID();
        UpdateExpenseRequest req = mock(UpdateExpenseRequest.class);
        when(expenseRepository.findById(id)).thenReturn(Optional.empty());
        // findById is a private call inside updateExpense via findById(id) public method; we must mock repository
        when(expenseRepository.findById(id)).thenReturn(Optional.empty());

        // calling updateExpense should trigger findById -> BusinessException
        assertThrows(BusinessException.class, () -> expenseService.updateExpense(id, req));
    }

    @Test
    void updateExpense_whenCategoryNotFound_shouldThrow() {
        UUID id = UUID.randomUUID();
        UUID catId = UUID.randomUUID();
        // prepare existing expense
        Expense existing = new Expense();
        existing.setId(id);
        existing.setEmployee(makeEmployeeWithDept(UUID.randomUUID(), BigDecimal.valueOf(1000)));
        existing.setCategory(makeCategory(null, null));
        existing.setAmount(BigDecimal.valueOf(10));
        existing.setDate(LocalDate.now());
        existing.setStatus(ExpenseStatus.PENDING);

        when(expenseRepository.findById(id)).thenReturn(Optional.of(existing));

        UpdateExpenseRequest req = mock(UpdateExpenseRequest.class);
        when(req.categoryId()).thenReturn(catId);

        when(categoryRepository.findById(catId)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class, () -> expenseService.updateExpense(id, req));
        assertTrue(ex.getMessage().toLowerCase().contains("category"));
    }

    @Test
    void updateExpense_whenValid_shouldSaveChanges() {
        UUID id = UUID.randomUUID();
        Category oldCat = makeCategory(null, null);
        Employee emp = makeEmployeeWithDept(UUID.randomUUID(), BigDecimal.valueOf(1000));
        Expense existing = new Expense();
        existing.setId(id);
        existing.setEmployee(emp);
        existing.setCategory(oldCat);
        existing.setAmount(BigDecimal.valueOf(10));
        existing.setDate(LocalDate.now());
        existing.setStatus(ExpenseStatus.PENDING);

        UUID newCatId = UUID.randomUUID();
        Category newCat = makeCategory(null, null);

        when(expenseRepository.findById(id)).thenReturn(Optional.of(existing));
        UpdateExpenseRequest req = mock(UpdateExpenseRequest.class);
        when(req.categoryId()).thenReturn(newCatId);
        when(req.amount()).thenReturn(BigDecimal.valueOf(20));
        when(req.date()).thenReturn(LocalDate.now());
        when(req.currency()).thenReturn("BRL");
        when(req.description()).thenReturn("Updated");

        when(categoryRepository.findById(newCatId)).thenReturn(Optional.of(newCat));
        when(expenseRepository.save(any(Expense.class))).thenAnswer(inv -> inv.getArgument(0));

        Expense updated = expenseService.updateExpense(id, req);

        assertEquals(newCat, updated.getCategory());
        assertEquals(BigDecimal.valueOf(20), updated.getAmount());
        assertEquals("Updated", updated.getDescription());
    }

    @Test
    void updateExpense_whenDailyLimitExceeded_shouldThrow() {
        UUID id = UUID.randomUUID();
        Category newCat = makeCategory(BigDecimal.valueOf(10), null); // daily limit 10
        Employee emp = makeEmployeeWithDept(UUID.randomUUID(), BigDecimal.valueOf(1000));
        Expense existing = new Expense();
        existing.setId(id);
        existing.setEmployee(emp);
        existing.setCategory(makeCategory(null, null));
        existing.setAmount(BigDecimal.valueOf(5));
        existing.setDate(LocalDate.now());
        existing.setStatus(ExpenseStatus.PENDING);

        when(expenseRepository.findById(id)).thenReturn(Optional.of(existing));

        UpdateExpenseRequest req = mock(UpdateExpenseRequest.class);
        when(req.categoryId()).thenReturn(newCat.getId());
        when(req.amount()).thenReturn(BigDecimal.valueOf(20)); // exceeds daily
        when(req.date()).thenReturn(LocalDate.now());

        when(categoryRepository.findById(newCat.getId())).thenReturn(Optional.of(newCat));

        BusinessException ex = assertThrows(BusinessException.class, () -> expenseService.updateExpense(id, req));
        assertTrue(ex.getMessage().toLowerCase().contains("daily"));
    }

    @Test
    void updateExpense_whenDepartmentBudgetExceeded_shouldThrow() {
        UUID id = UUID.randomUUID();
        Department dept = new Department();
        dept.setId(UUID.randomUUID());
        dept.setMonthlyBudget(BigDecimal.valueOf(100));
        Employee emp = new Employee();
        emp.setId(UUID.randomUUID());
        emp.setDepartment(dept);

        Category cat = makeCategory(null, null);

        Expense existing = new Expense();
        existing.setId(id);
        existing.setEmployee(emp);
        existing.setCategory(cat);
        existing.setAmount(BigDecimal.valueOf(10));
        existing.setDate(LocalDate.now());
        existing.setStatus(ExpenseStatus.PENDING);

        when(expenseRepository.findById(id)).thenReturn(Optional.of(existing));

        UpdateExpenseRequest req = mock(UpdateExpenseRequest.class);
        when(req.categoryId()).thenReturn(cat.getId());
        when(req.amount()).thenReturn(BigDecimal.valueOf(95)); // large amount
        when(req.date()).thenReturn(LocalDate.now());

        when(categoryRepository.findById(cat.getId())).thenReturn(Optional.of(cat));

        // alreadySpentExcluding = 10 (existing) -> 10 + 95 = 105 > 100
        when(expenseRepository.sumAmountByDepartmentAndMonthExcluding(eq(dept.getId()), any(), any(), any(), eq(existing.getId())))
                .thenReturn(BigDecimal.valueOf(10));

        BusinessException ex = assertThrows(BusinessException.class, () -> expenseService.updateExpense(id, req));
        assertTrue(ex.getMessage().toLowerCase().contains("monthly budget"));
    }

    @Test
    void attachReceipt_whenExpenseNotFound_shouldThrow() {
        UUID id = UUID.randomUUID();
        when(expenseRepository.findById(id)).thenReturn(Optional.empty());
        MultipartFile file = mock(MultipartFile.class);

        assertThrows(BusinessException.class, () -> expenseService.attachReceipt(id, file));
    }

    @Test
    void attachReceipt_whenValid_shouldStoreAndReturnExpenseWithReceipt() {
        UUID id = UUID.randomUUID();
        Expense expense = new Expense();
        expense.setId(id);
        expense.setEmployee(makeEmployeeWithDept(UUID.randomUUID(), BigDecimal.valueOf(1000)));
        when(expenseRepository.findById(id)).thenReturn(Optional.of(expense));

        MultipartFile file = mock(MultipartFile.class);
        when(receiptStorageService.store(id, file)).thenReturn("stored-file.jpg");
        when(expenseRepository.save(any(Expense.class))).thenAnswer(inv -> inv.getArgument(0));

        Expense updated = expenseService.attachReceipt(id, file);

        assertEquals("stored-file.jpg", updated.getReceiptFilename());
        assertNotNull(updated.getReceiptUrl());
        verify(receiptStorageService, times(1)).store(id, file);
    }

    @Test
    void loadReceipt_whenExpenseNotFound_shouldThrow() {
        UUID id = UUID.randomUUID();
        when(expenseRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(BusinessException.class, () -> expenseService.loadReceipt(id));
    }

    @Test
    void loadReceipt_whenExists_shouldReturnResourceFromStorage() {
        UUID id = UUID.randomUUID();
        Expense expense = new Expense();
        expense.setId(id);
        expense.setReceiptFilename("f.jpg");
        when(expenseRepository.findById(id)).thenReturn(Optional.of(expense));
        Resource resource = mock(Resource.class);
        when(receiptStorageService.loadAsResource("f.jpg")).thenReturn(resource);

        Resource r = expenseService.loadReceipt(id);
        assertEquals(resource, r);
        verify(receiptStorageService, times(1)).loadAsResource("f.jpg");
    }

    @Test
    void approveByManager_whenNotPending_shouldThrow() {
        UUID id = UUID.randomUUID();
        Expense e = new Expense();
        e.setId(id);
        e.setStatus(ExpenseStatus.APPROVED_MANAGER); // not PENDING

        when(expenseRepository.findById(id)).thenReturn(Optional.of(e));

        assertThrows(BusinessException.class, () -> expenseService.approveByManager(id, UUID.randomUUID()));
    }

    @Test
    void approveByManager_whenPending_shouldSetApprovedManager() {
        UUID id = UUID.randomUUID();
        Expense e = new Expense();
        e.setId(id);
        e.setStatus(ExpenseStatus.PENDING);

        when(expenseRepository.findById(id)).thenReturn(Optional.of(e));
        when(expenseRepository.save(any(Expense.class))).thenAnswer(inv -> inv.getArgument(0));

        Expense saved = expenseService.approveByManager(id, UUID.randomUUID());

        assertEquals(ExpenseStatus.APPROVED_MANAGER, saved.getStatus());
        verify(expenseRepository, times(1)).save(e);
    }

    @Test
    void approveByFinance_whenNotApprovedByManager_shouldThrow() {
        UUID id = UUID.randomUUID();
        Expense e = new Expense();
        e.setId(id);
        e.setStatus(ExpenseStatus.PENDING);

        when(expenseRepository.findById(id)).thenReturn(Optional.of(e));

        assertThrows(BusinessException.class, () -> expenseService.approveByFinance(id, UUID.randomUUID()));
    }

    @Test
    void approveByFinance_whenApprovedByManager_shouldSetApprovedFinanceAndClearNeedsReview() {
        UUID id = UUID.randomUUID();
        Expense e = new Expense();
        e.setId(id);
        e.setStatus(ExpenseStatus.APPROVED_MANAGER);
        e.setNeedsReview(true);

        when(expenseRepository.findById(id)).thenReturn(Optional.of(e));
        when(expenseRepository.save(any(Expense.class))).thenAnswer(inv -> inv.getArgument(0));

        Expense saved = expenseService.approveByFinance(id, UUID.randomUUID());

        assertEquals(ExpenseStatus.APPROVED_FINANCE, saved.getStatus());
        assertFalse(saved.isNeedsReview());
    }

    @Test
    void rejectExpense_whenAlreadyFinalized_shouldThrow() {
        UUID id = UUID.randomUUID();
        Expense e = new Expense();
        e.setId(id);
        e.setStatus(ExpenseStatus.APPROVED_FINANCE);

        when(expenseRepository.findById(id)).thenReturn(Optional.of(e));

        assertThrows(BusinessException.class, () -> expenseService.rejectExpense(id, UUID.randomUUID(), "reason"));
    }

    @Test
    void rejectExpense_whenValid_shouldSetRejected() {
        UUID id = UUID.randomUUID();
        Expense e = new Expense();
        e.setId(id);
        e.setStatus(ExpenseStatus.PENDING);
        e.setNeedsReview(true);

        when(expenseRepository.findById(id)).thenReturn(Optional.of(e));
        when(expenseRepository.save(any(Expense.class))).thenAnswer(inv -> inv.getArgument(0));

        Expense saved = expenseService.rejectExpense(id, UUID.randomUUID(), "not OK");

        assertEquals(ExpenseStatus.REJECTED, saved.getStatus());
        assertFalse(saved.isNeedsReview());
    }

    @Test
    void findAll_shouldDelegateToRepository() {
        List<Expense> list = Arrays.asList(new Expense(), new Expense());
        when(expenseRepository.findAll()).thenReturn(list);
        List<Expense> res = expenseService.findAll();
        assertEquals(2, res.size());
    }

    @Test
    void findById_whenNotFound_shouldThrow() {
        UUID id = UUID.randomUUID();
        when(expenseRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(BusinessException.class, () -> expenseService.findById(id));
    }

    @Test
    void findById_whenFound_shouldReturn() {
        UUID id = UUID.randomUUID();
        Expense e = new Expense();
        e.setId(id);
        when(expenseRepository.findById(id)).thenReturn(Optional.of(e));
        Expense res = expenseService.findById(id);
        assertEquals(e, res);
    }
}
