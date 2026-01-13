package com.ubs.expensemanager.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.ubs.expensemanager.domain.Department;
import com.ubs.expensemanager.domain.Employee;
import com.ubs.expensemanager.dto.employee.CreateEmployeeRequest;
import com.ubs.expensemanager.dto.employee.UpdateEmployeeRequest;
import com.ubs.expensemanager.exception.BusinessException;
import com.ubs.expensemanager.repository.DepartmentRepository;
import com.ubs.expensemanager.repository.ExpenseRepository;
import com.ubs.expensemanager.repository.EmployeeRepository;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final ExpenseRepository expenseRepository;

    // simple email regex (practical, not RFC-perfect)
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    public EmployeeService(EmployeeRepository employeeRepository,
            DepartmentRepository departmentRepository,
            ExpenseRepository expenseRepository) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
        this.expenseRepository = expenseRepository;
    }

    @Transactional
    @SuppressWarnings("null")
    public Employee createEmployee(CreateEmployeeRequest req) {
        validateEmployeeFields(req.name(), req.email(), req.position());

        if (!EMAIL_PATTERN.matcher(req.email()).matches()) {
            throw new BusinessException("Invalid email format");
        }

        // unique email check
        Optional<Employee> byEmail = employeeRepository.findByEmail(req.email());
        if (byEmail.isPresent()) {
            throw new BusinessException("Email already in use");
        }

        Employee e = new Employee();
        e.setName(req.name());
        e.setEmail(req.email());
        e.setPosition(req.position());

        if (req.departmentId() != null) {
            Department dept = departmentRepository.findById(req.departmentId())
                    .orElseThrow(() -> new BusinessException("Department not found"));
            e.setDepartment(dept);
        }

        if (req.managerId() != null) {
            Employee manager = employeeRepository.findById(req.managerId())
                    .orElseThrow(() -> new BusinessException("Manager not found"));
            // prevent self-manager
            // (managerId should not equal the new employee id — here new employee has no id
            // yet)
            // additional check: manager != subordinate cycles could be checked in update
            // time
            e.setManager(manager);
        }

        return employeeRepository.save(e);
    }

    public List<Employee> findAll() {
        return employeeRepository.findAll();
    }

    @Transactional
    @SuppressWarnings("null")
    public Employee updateEmployee(UUID id, UpdateEmployeeRequest req) {
        Employee existing = employeeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found"));

        validateEmployeeFields(req.name(), req.email(), req.position());

        if (!EMAIL_PATTERN.matcher(req.email()).matches()) {
            throw new BusinessException("Invalid email format");
        }

        // If email changed, ensure uniqueness
        if (!existing.getEmail().equalsIgnoreCase(req.email())) {
            Optional<Employee> other = employeeRepository.findByEmail(req.email());
            if (other.isPresent() && !other.get().getId().equals(id)) {
                throw new BusinessException("Email already in use by another employee");
            }
        }

        existing.setName(req.name());
        existing.setEmail(req.email());
        existing.setPosition(req.position());

        if (req.departmentId() != null) {
            Department dept = departmentRepository.findById(req.departmentId())
                    .orElseThrow(() -> new BusinessException("Department not found"));
            existing.setDepartment(dept);
        } else {
            existing.setDepartment(null);
        }

        if (req.managerId() != null) {
            if (req.managerId().equals(id)) {
                throw new BusinessException("Employee cannot be their own manager");
            }
            Employee manager = employeeRepository.findById(req.managerId())
                    .orElseThrow(() -> new BusinessException("Manager not found"));
            // optional: enforce same department for manager and employee
            if (existing.getDepartment() != null && manager.getDepartment() != null
                    && !existing.getDepartment().getId().equals(manager.getDepartment().getId())) {
                throw new BusinessException("Manager must belong to the same department");
            }
            existing.setManager(manager);
        } else {
            existing.setManager(null);
        }

        return employeeRepository.save(existing);
    }

    @SuppressWarnings("null")
    public Employee findById(UUID id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found"));
    }

    @Transactional
    @SuppressWarnings("null")
    public void deleteEmployee(UUID id) {
        Employee existing = employeeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found"));

        if (employeeRepository.existsByManager_Id(id)) {
            throw new BusinessException("Cannot delete employee who manages other employees");
        }

        if (expenseRepository.existsByEmployee_Id(id)) {
            throw new BusinessException("Cannot delete employee with registered expenses");
        }

        employeeRepository.delete(existing);
    }

    private void validateEmployeeFields(String name, String email, String position) {
        if (name == null || name.isBlank()) {
            throw new BusinessException("Employee name must be provided");
        }
        if (email == null || email.isBlank()) {
            throw new BusinessException("Employee email must be provided");
        }
        if (position == null || position.isBlank()) {
            throw new BusinessException("Employee position must be provided");
        }
    }
}
