package com.ubs.expensemanager.service;

import com.ubs.expensemanager.domain.Department;
import com.ubs.expensemanager.domain.Employee;
import com.ubs.expensemanager.domain.enums.EmployeeRole;
import com.ubs.expensemanager.dto.employee.CreateEmployeeRequest;
import com.ubs.expensemanager.dto.employee.UpdateEmployeeRequest;
import com.ubs.expensemanager.exception.BusinessException;
import com.ubs.expensemanager.repository.DepartmentRepository;
import com.ubs.expensemanager.repository.EmployeeRepository;
import com.ubs.expensemanager.repository.ExpenseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.mockito.stubbing.Answer;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmployeeServiceTest {

    @Mock private EmployeeRepository employeeRepository;
    @Mock private DepartmentRepository departmentRepository;
    @Mock private ExpenseRepository expenseRepository;

    @InjectMocks private EmployeeService employeeService;

    @BeforeEach
    void setUp() { MockitoAnnotations.openMocks(this); }

    private Answer<Employee> saveAnswer() {
        return inv -> {
            Employee e = inv.getArgument(0);
            if (e.getId() == null) e.setId(UUID.randomUUID());
            return e;
        };
    }

    @Test
    void createEmployee_valid_shouldSave() {
        CreateEmployeeRequest req = mock(CreateEmployeeRequest.class);
        when(req.name()).thenReturn("Alice");
        when(req.email()).thenReturn("alice@example.com");
        when(req.position()).thenReturn("Eng");
        when(req.role()).thenReturn(EmployeeRole.MANAGER);
        when(req.departmentId()).thenReturn(null);
        when(req.managerId()).thenReturn(null);

        when(employeeRepository.findByEmail("alice@example.com")).thenReturn(Optional.empty());
        when(employeeRepository.save(any())).thenAnswer(saveAnswer());

        Employee saved = employeeService.createEmployee(req);

        assertNotNull(saved.getId());
        assertEquals("alice@example.com", saved.getEmail());
    }

    @Test
    void createEmployee_invalidEmail_shouldThrow() {
        CreateEmployeeRequest req = mock(CreateEmployeeRequest.class);
        when(req.name()).thenReturn("Bob");
        when(req.email()).thenReturn("bad-email");
        when(req.position()).thenReturn("Eng");
        when(req.role()).thenReturn(EmployeeRole.MANAGER);

        assertThrows(BusinessException.class, () -> employeeService.createEmployee(req));
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void createEmployee_emailAlreadyUsed_shouldThrow() {
        CreateEmployeeRequest req = mock(CreateEmployeeRequest.class);
        when(req.name()).thenReturn("Carol");
        when(req.email()).thenReturn("carol@example.com");
        when(req.position()).thenReturn("Eng");
        when(req.role()).thenReturn(EmployeeRole.MANAGER);

        Employee existing = new Employee();
        existing.setId(UUID.randomUUID());
        existing.setEmail("carol@example.com");
        when(employeeRepository.findByEmail("carol@example.com")).thenReturn(Optional.of(existing));

        assertThrows(BusinessException.class, () -> employeeService.createEmployee(req));
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void updateEmployee_notFound_shouldThrow404() {
        UUID id = UUID.randomUUID();
        UpdateEmployeeRequest req = mock(UpdateEmployeeRequest.class);
        when(employeeRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> employeeService.updateEmployee(id, req));
    }

    @Test
    void updateEmployee_emailUsedByOther_shouldThrow() {
        UUID id = UUID.randomUUID();
        Employee existing = new Employee(); existing.setId(id); existing.setEmail("old@example.com");
        when(employeeRepository.findById(id)).thenReturn(Optional.of(existing));

        UpdateEmployeeRequest req = mock(UpdateEmployeeRequest.class);
        when(req.name()).thenReturn("X");
        when(req.email()).thenReturn("other@example.com");
        when(req.position()).thenReturn("Eng");
        when(req.role()).thenReturn(EmployeeRole.MANAGER);

        Employee other = new Employee(); other.setId(UUID.randomUUID()); other.setEmail("other@example.com");
        when(employeeRepository.findByEmail("other@example.com")).thenReturn(Optional.of(other));

        assertThrows(BusinessException.class, () -> employeeService.updateEmployee(id, req));
    }

    @Test
    void deleteEmployee_withExpenses_shouldThrow() {
        UUID id = UUID.randomUUID();
        Employee e = new Employee(); e.setId(id);
        when(employeeRepository.findById(id)).thenReturn(Optional.of(e));
        when(employeeRepository.existsByManager_Id(id)).thenReturn(false);
        when(expenseRepository.existsByEmployee_Id(id)).thenReturn(true);

        assertThrows(BusinessException.class, () -> employeeService.deleteEmployee(id));
        verify(employeeRepository, never()).delete(any());
    }

    @Test
    void deleteEmployee_noConstraints_shouldDelete() {
        UUID id = UUID.randomUUID();
        Employee e = new Employee(); e.setId(id);
        when(employeeRepository.findById(id)).thenReturn(Optional.of(e));
        when(employeeRepository.existsByManager_Id(id)).thenReturn(false);
        when(expenseRepository.existsByEmployee_Id(id)).thenReturn(false);

        doNothing().when(employeeRepository).delete(e);
        employeeService.deleteEmployee(id);
        verify(employeeRepository).delete(e);
    }

    @Test
    void findAll_delegatesToRepository() {
        List<Employee> list = Arrays.asList(new Employee(), new Employee());
        when(employeeRepository.findAll()).thenReturn(list);
        assertEquals(2, employeeService.findAll().size());
    }
}
