package com.ubs.expensemanager.controller;

import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ubs.expensemanager.domain.Department;
import com.ubs.expensemanager.dto.department.CreateDepartmentRequest;
import com.ubs.expensemanager.dto.department.DepartmentResponse;
import com.ubs.expensemanager.dto.department.UpdateDepartmentRequest;
import com.ubs.expensemanager.mapper.DepartmentMapper;
import com.ubs.expensemanager.service.DepartmentService;

@RestController
@RequestMapping("/department")
public class DepartmentController {

    private final DepartmentService service;
    private final DepartmentMapper mapper;

    public DepartmentController (DepartmentService service, DepartmentMapper mapper){
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping("/{id}")
    public DepartmentResponse getDepartment(@PathVariable UUID id){
        Department department = service.findById(id);
        return mapper.toResponse(department); 
    }

    @PostMapping
    public DepartmentResponse newDepartment(@RequestBody CreateDepartmentRequest request){
        Department department = service.createDepartment(request);
        return mapper.toResponse(department);
    }

    @PatchMapping("/{id}")
    public DepartmentResponse updateDepartment (@PathVariable UUID id, @RequestBody UpdateDepartmentRequest request){
        Department department = service.updateDepartment(id, request);
        return mapper.toResponse(department);
    }
}
