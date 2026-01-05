package com.ubs.expensemanager.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ubs.expensemanager.domain.Department;
import com.ubs.expensemanager.repository.DepartmentRepository;

@Service
public class DepartmentService {

    private final DepartmentRepository repository;

    public DepartmentService(DepartmentRepository repository){
        this.repository = repository;
    }

    public Department createDepartment (Department department){
        validateDepartment(department);
        return department;
    }

    public List<Department> findAll(){
        return repository.findAll();
    }

    private void validateDepartment (Department department){
        //validacao - work in progress
    }
}
