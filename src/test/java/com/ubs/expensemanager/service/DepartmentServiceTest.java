package com.ubs.expensemanager.service;

import com.ubs.expensemanager.domain.Department;
import com.ubs.expensemanager.exception.BusinessException;
import com.ubs.expensemanager.repository.DepartmentRepository;
import com.ubs.expensemanager.repository.ExpenseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DepartmentServiceTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private ExpenseRepository expenseRepository;

    @InjectMocks
    private DepartmentService departmentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createDepartment_whenValid_shouldSaveAndReturn() {
        // given
        Department d = new Department();
        d.setId(UUID.randomUUID());
        d.setName("TI");
        d.setMonthlyBudget(BigDecimal.valueOf(10000));

        when(departmentRepository.save(any(Department.class))).thenAnswer(invocation -> {
            Department arg = invocation.getArgument(0);
            if (arg.getId() == null) arg.setId(UUID.randomUUID());
            return arg;
        });

        // when
        com.ubs.expensemanager.dto.department.CreateDepartmentRequest req = mock(com.ubs.expensemanager.dto.department.CreateDepartmentRequest.class);
        when(req.name()).thenReturn("TI");
        when(req.monthlyBudget()).thenReturn(BigDecimal.valueOf(10000));

        Department saved = departmentService.createDepartment(req);

        // then
        assertNotNull(saved.getId());
        assertEquals("TI", saved.getName());
        assertEquals(BigDecimal.valueOf(10000), saved.getMonthlyBudget());
        verify(departmentRepository, times(1)).save(any(Department.class));
    }

    @Test
    void createDepartment_whenInvalidName_shouldThrow() {
        com.ubs.expensemanager.dto.department.CreateDepartmentRequest req = mock(com.ubs.expensemanager.dto.department.CreateDepartmentRequest.class);
        when(req.name()).thenReturn("   ");
        when(req.monthlyBudget()).thenReturn(BigDecimal.valueOf(1000));

        BusinessException ex = assertThrows(BusinessException.class, () -> departmentService.createDepartment(req));
        assertTrue(ex.getMessage().toLowerCase().contains("name"));
    }

    @Test
    void updateDepartment_whenReducingBudgetAndAlreadySpentExceedsNewBudget_shouldThrow() {
        UUID deptId = UUID.randomUUID();

        Department existing = new Department();
        existing.setId(deptId);
        existing.setName("Comercial");
        existing.setMonthlyBudget(BigDecimal.valueOf(5000));

        when(departmentRepository.findById(deptId)).thenReturn(Optional.of(existing));

        com.ubs.expensemanager.dto.department.UpdateDepartmentRequest req = mock(com.ubs.expensemanager.dto.department.UpdateDepartmentRequest.class);
        when(req.name()).thenReturn("Comercial");
        when(req.monthlyBudget()).thenReturn(BigDecimal.valueOf(1000)); // reduce budget

        // simulate already spent this month = 2000
        LocalDate start = YearMonth.now().atDay(1);
        LocalDate end = YearMonth.now().atEndOfMonth();
        when(expenseRepository.sumAmountByDepartmentAndMonth(eq(existing.getId()), eq(start), eq(end), any()))
                .thenReturn(BigDecimal.valueOf(2000));

        BusinessException ex = assertThrows(BusinessException.class, () -> departmentService.updateDepartment(deptId, req));
        assertTrue(ex.getMessage().toLowerCase().contains("cannot reduce budget") || ex.getMessage().toLowerCase().contains("exceeds new monthly budget"));
    }

    @Test
    void updateDepartment_whenNotFound_shouldThrow404() {
        UUID deptId = UUID.randomUUID();
        when(departmentRepository.findById(deptId)).thenReturn(Optional.empty());

        com.ubs.expensemanager.dto.department.UpdateDepartmentRequest req = mock(com.ubs.expensemanager.dto.department.UpdateDepartmentRequest.class);
        when(req.name()).thenReturn("Ops");
        when(req.monthlyBudget()).thenReturn(BigDecimal.valueOf(1000));

        assertThrows(ResponseStatusException.class, () -> departmentService.updateDepartment(deptId, req));
    }

    @Test
    void calculateMonthlyUsage_whenRepositoryReturnsNull_shouldReturnZero() {
        UUID deptId = UUID.randomUUID();
        YearMonth ym = YearMonth.of(2025, 1);
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();

        when(expenseRepository.sumAmountByDepartmentAndMonth(eq(deptId), eq(start), eq(end), any()))
                .thenReturn(null);

        BigDecimal used = departmentService.calculateMonthlyUsage(deptId, ym);
        assertNotNull(used);
        assertEquals(BigDecimal.ZERO, used);
    }

    @Test
    void calculateMonthlyUsage_whenRepositoryReturnsValue_shouldReturnThatValue() {
        UUID deptId = UUID.randomUUID();
        YearMonth ym = YearMonth.of(2025, 1);
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();

        when(expenseRepository.sumAmountByDepartmentAndMonth(eq(deptId), eq(start), eq(end), any()))
                .thenReturn(BigDecimal.valueOf(1234.56));

        BigDecimal used = departmentService.calculateMonthlyUsage(deptId, ym);
        assertEquals(BigDecimal.valueOf(1234.56), used);
    }

    @Test
    void getMonthlyUsageForAll_shouldMapResultsToMapUuidToBigDecimal() {
        YearMonth ym = YearMonth.of(2025, 1);
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();

        UUID d1 = UUID.randomUUID();
        UUID d2 = UUID.randomUUID();

        Object[] r1 = new Object[] { d1, BigDecimal.valueOf(1000) };
        Object[] r2 = new Object[] { d2, BigDecimal.valueOf(2000) };

        List<Object[]> repoResults = Arrays.asList(r1, r2);

        when(expenseRepository.sumAmountByDepartmentBetween(eq(start), eq(end), any()))
                .thenReturn(repoResults);

        Map<UUID, BigDecimal> map = departmentService.getMonthlyUsageForAll(ym);

        assertEquals(2, map.size());
        assertEquals(BigDecimal.valueOf(1000), map.get(d1));
        assertEquals(BigDecimal.valueOf(2000), map.get(d2));
    }
}
