package com.ubs.expensemanager.controller;

import java.net.URI;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ubs.expensemanager.domain.Department;
import com.ubs.expensemanager.dto.department.CreateDepartmentRequest;
import com.ubs.expensemanager.dto.department.DepartmentResponse;
import com.ubs.expensemanager.dto.department.UpdateDepartmentRequest;
import com.ubs.expensemanager.mapper.DepartmentMapper;
import com.ubs.expensemanager.service.DepartmentService;

@RestController
@RequestMapping("/api/departments")
public class DepartmentController {

    private final DepartmentService departmentService;
    private final DepartmentMapper departmentMapper;

    public DepartmentController(
            DepartmentService departmentService,
            DepartmentMapper departmentMapper
    ) {
        this.departmentService = departmentService;
        this.departmentMapper = departmentMapper;
    }

    /**
     * GET /api/departments
     * Returns all departments with their current monthly budget usage.
     */
    @GetMapping
    public ResponseEntity<List<DepartmentResponse>> listDepartments() {
        List<Department> departments = departmentService.findAll();
        YearMonth currentMonth = YearMonth.now();

        // Map: departmentId -> total spent in the current month
        Map<UUID, BigDecimal> usageMap =
                departmentService.getMonthlyUsageForAll(currentMonth);

        List<DepartmentResponse> responses = departments.stream()
            .map(dept -> departmentMapper.toResponse(
                    dept,
                    usageMap.getOrDefault(dept.getId(), BigDecimal.ZERO)
            ))
            .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    /**
     * GET /api/departments/{id}
     * Returns a single department with its current monthly budget usage.
     */
    @GetMapping("/{id}")
    public ResponseEntity<DepartmentResponse> getDepartment(@PathVariable UUID id) {
        Department department = departmentService.findById(id);

        BigDecimal used =
                departmentService.calculateMonthlyUsage(department.getId(), YearMonth.now());

        DepartmentResponse response =
                departmentMapper.toResponse(department, used);

        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/departments
     * Creates a new department and returns 201 Created with Location header.
     */
    @PostMapping
    public ResponseEntity<DepartmentResponse> createDepartment(
            @RequestBody CreateDepartmentRequest request
    ) {
        Department created = departmentService.createDepartment(request);

        // Newly created departments have no expenses yet
        DepartmentResponse response =
                departmentMapper.toResponse(created, BigDecimal.ZERO);

        URI location = URI.create("/api/departments/" + created.getId());
        return ResponseEntity.created(location).body(response);
    }

    /**
     * PATCH /api/departments/{id}
     * Updates department data (partial update).
     */
    @PatchMapping("/{id}")
    public ResponseEntity<DepartmentResponse> updateDepartment(
            @PathVariable UUID id,
            @RequestBody UpdateDepartmentRequest request
    ) {
        Department updated = departmentService.updateDepartment(id, request);

        BigDecimal used =
                departmentService.calculateMonthlyUsage(updated.getId(), YearMonth.now());

        DepartmentResponse response =
                departmentMapper.toResponse(updated, used);

        return ResponseEntity.ok(response);
    }
}
