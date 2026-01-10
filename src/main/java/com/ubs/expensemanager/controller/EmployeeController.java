package com.ubs.expensemanager.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ubs.expensemanager.domain.Employee;
import com.ubs.expensemanager.dto.employee.CreateEmployeeRequest;
import com.ubs.expensemanager.dto.employee.EmployeeResponse;
import com.ubs.expensemanager.dto.employee.UpdateEmployeeRequest;
import com.ubs.expensemanager.mapper.EmployeeMapper;
import com.ubs.expensemanager.service.EmployeeService;


@RestController
@RequestMapping("/employee")
public class EmployeeController {

    private final EmployeeService service;
    private final EmployeeMapper mapper;

    public EmployeeController (EmployeeService service, EmployeeMapper mapper){
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
    public List <EmployeeResponse> getEmployees() {
        List<Employee> employees = service.findAll();
        return mapper.toResponseList(employees);
    }
    
    @GetMapping("/{id}")
    public EmployeeResponse getEmployeeById(@PathVariable UUID id){
        Employee employee = service.findById(id);
        return mapper.toResponse(employee); 
    }

    @PostMapping
    public EmployeeResponse newEmployee(@RequestBody CreateEmployeeRequest request){
        Employee employee = service.createEmployee(request);
        return mapper.toResponse(employee);
    }

    @PatchMapping("/{id}")
    public EmployeeResponse updateEmployee (@PathVariable UUID id, @RequestBody UpdateEmployeeRequest request){
        Employee employee = service.updateEmployee(id, request);
        return mapper.toResponse(employee);
    }
}
