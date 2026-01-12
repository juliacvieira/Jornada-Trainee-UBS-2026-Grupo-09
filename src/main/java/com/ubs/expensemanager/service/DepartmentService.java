package com.ubs.expensemanager.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.ubs.expensemanager.domain.Department;
import com.ubs.expensemanager.dto.department.CreateDepartmentRequest;
import com.ubs.expensemanager.dto.department.UpdateDepartmentRequest;
import com.ubs.expensemanager.exception.BusinessException;
import com.ubs.expensemanager.repository.DepartmentRepository;
import com.ubs.expensemanager.repository.ExpenseRepository;

@Service
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final ExpenseRepository expenseRepository;

    public DepartmentService(DepartmentRepository departmentRepository,
                             ExpenseRepository expenseRepository) {
        this.departmentRepository = departmentRepository;
        this.expenseRepository = expenseRepository;
    }

    @Transactional
    public Department createDepartment(CreateDepartmentRequest req) {
        validateDepartmentFields(req.name(), req.monthlyBudget());

        Department d = new Department();
        d.setName(req.name());
        d.setMonthlyBudget(req.monthlyBudget());
        return departmentRepository.save(d);
    }

    public List<Department> findAll() {
        return departmentRepository.findAll();
    }

    @Transactional
    public Department updateDepartment(UUID id, UpdateDepartmentRequest req) {
        Department existing = departmentRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Department not found"));

        validateDepartmentFields(req.name(), req.monthlyBudget());

        // If budget is being reduced, ensure current approved spending for the relevant period doesn't exceed new budget
        BigDecimal newBudget = req.monthlyBudget();
        BigDecimal oldBudget = existing.getMonthlyBudget();
        if (newBudget == null) {
            throw new BusinessException("Monthly budget must be provided");
        }
        if (newBudget.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("Monthly budget must be non-negative");
        }

        // Only check if reduced (or always check if you prefer)
        if (oldBudget == null || newBudget.compareTo(oldBudget) < 0) {
            // We'll check the current month (you can change to other period if needed)
            YearMonth ym = YearMonth.now();
            LocalDate start = ym.atDay(1);
            LocalDate end = ym.atEndOfMonth();

            BigDecimal alreadySpent = expenseRepository.sumAmountByDepartmentAndMonth(
                existing.getId(), start, end, /*status*/ com.ubs.expensemanager.domain.enums.ExpenseStatus.APPROVED_FINANCE);

            if (alreadySpent == null) alreadySpent = BigDecimal.ZERO;

            if (alreadySpent.compareTo(newBudget) > 0) {
                throw new BusinessException("Cannot reduce budget: current approved spending this month (" +
                    alreadySpent + ") exceeds new monthly budget (" + newBudget + ")");
            }
        }

        existing.setName(req.name());
        existing.setMonthlyBudget(req.monthlyBudget());
        return departmentRepository.save(existing);
    }

    public Department findById(UUID id) {
        return departmentRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Department not found"));
    }
    
    public BigDecimal calculateMonthlyUsage(UUID departmentId, YearMonth yearMonth) {
        LocalDate start = yearMonth.atDay(1);
        LocalDate end = yearMonth.atEndOfMonth();

        BigDecimal used = expenseRepository.sumAmountByDepartmentAndMonth(
                departmentId,
                start,
                end,
                com.ubs.expensemanager.domain.enums.ExpenseStatus.APPROVED_FINANCE
        );

        return used != null ? used : BigDecimal.ZERO;
    }

   
    public Map<UUID, BigDecimal> getMonthlyUsageForAll(YearMonth yearMonth) {
        LocalDate start = yearMonth.atDay(1);
        LocalDate end = yearMonth.atEndOfMonth();

        List<Object[]> results = expenseRepository.sumAmountByDepartmentBetween(
                start,
                end,
                com.ubs.expensemanager.domain.enums.ExpenseStatus.APPROVED_FINANCE
        );

        return results.stream()
                .collect(Collectors.toMap(
                        r -> (UUID) r[0],
                        r -> (BigDecimal) r[1]
                ));
    }

    private void validateDepartmentFields(String name, BigDecimal monthlyBudget) {
        if (name == null || name.isBlank()) {
            throw new BusinessException("Department name must be provided");
        }
        if (monthlyBudget == null) {
            throw new BusinessException("Monthly budget must be provided");
        }
        if (monthlyBudget.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("Monthly budget must be non-negative");
        }
    }
}
