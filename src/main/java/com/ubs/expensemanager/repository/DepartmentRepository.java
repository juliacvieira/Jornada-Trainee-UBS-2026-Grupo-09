package com.ubs.expensemanager.repository;

import com.ubs.expensemanager.domain.Department;
import com.ubs.expensemanager.domain.Employee;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, UUID> {
	Optional<Employee> findByEmail(String email);
}
