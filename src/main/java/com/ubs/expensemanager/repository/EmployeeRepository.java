package com.ubs.expensemanager.repository;

import com.ubs.expensemanager.domain.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface EmployeeRepository extends JpaRepository<Employee, UUID> {
}
