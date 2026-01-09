package com.ubs.expensemanager.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.ubs.expensemanager.domain.Employee;
import com.ubs.expensemanager.dto.employee.CreateEmployeeRequest;
import com.ubs.expensemanager.dto.employee.UpdateEmployeeRequest;
import com.ubs.expensemanager.repository.EmployeeRepository;

@Service
public class EmployeeService {

    private final EmployeeRepository repository;

    public EmployeeService(EmployeeRepository repository){
        this.repository = repository;
    }

    public Employee createEmployee (CreateEmployeeRequest request){
        Employee employee = new Employee();

        employee.setName(request.name());
        employee.setDepartment(request.department());
        employee.setEmail(request.email());
        employee.setManager(request.manager());
        employee.setPosition(request.position());

        boolean valid = validateEmployee(employee);

        try {
            if (valid == true) {
            Employee saved = repository.save(employee);
            return saved;
        }
        } catch (Exception e) {
            System.out.println("Validation exception: " + e);
        }

        return employee;
    }

    public Employee updateEmployee(UUID id, UpdateEmployeeRequest request){
        Employee employee = findById(id);

        //acrescentar validação do que está sendo realmente mudado
        employee.setDepartment(request.department());
        employee.setEmail(request.email());
        employee.setManager(request.manager());
        employee.setPosition(request.position());

        return employee;
    }

    public List<Employee> findAll(){
        return repository.findAll();
    }

    public Employee findById(UUID id){
        return repository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("Employee" + id + "not found"));
    }

    private boolean validateEmployee (Employee employee){
        //validacao - work in progress

        return true;
    }
}
