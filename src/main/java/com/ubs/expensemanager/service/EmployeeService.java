package com.ubs.expensemanager.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ubs.expensemanager.domain.Employee;
import com.ubs.expensemanager.repository.EmployeeRepository;

@Service
public class EmployeeService {

    private final EmployeeRepository repository;

    public EmployeeService(EmployeeRepository repository){
        this.repository = repository;
    }

    public Employee createEmployee (Employee employee){
        validateEmployee(employee);
        return employee;
    }

    public List<Employee> findAll(){
        return repository.findAll();
    }

    private void validateEmployee (Employee employee){
        //validacao - work in progress
    }
}
