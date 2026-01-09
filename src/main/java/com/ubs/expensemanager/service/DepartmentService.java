package com.ubs.expensemanager.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.ubs.expensemanager.domain.Department;
import com.ubs.expensemanager.dto.department.CreateDepartmentRequest;
import com.ubs.expensemanager.dto.department.UpdateDepartmentRequest;
import com.ubs.expensemanager.repository.DepartmentRepository;

@Service
public class DepartmentService {

    private final DepartmentRepository repository;

    public DepartmentService(DepartmentRepository repository){
        this.repository = repository;
    }

    public Department createDepartment (CreateDepartmentRequest request){
        Department department = new Department();

        department.setName(request.name());
        department.setMonthlyBudget(request.monthlyBudget());

        boolean valid = validateDepartment(department);

        try {
            if (valid == true) {
            Department saved = repository.save(department);
            return saved;
        }
        } catch (Exception e) {
            System.out.println("Validation exception: " + e);
        }

        return department;
    }

    public Department updateDepartment (UUID id, UpdateDepartmentRequest request){
        Department department = findById(id);

        //acrescentar validação
        department.setName(request.name());
        department.setMonthlyBudget(request.monthlyBudget());

        return department;
    }

    public List<Department> findAll(){
        return repository.findAll();
    }

    public Department findById(UUID id){
        return repository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("Employee" + id + "not found"));
    }

    private boolean validateDepartment (Department department){
        //validacao - work in progress

        return true;
    }
}
