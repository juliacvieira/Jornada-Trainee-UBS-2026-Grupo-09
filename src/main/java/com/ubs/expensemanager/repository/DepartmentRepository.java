package com.ubs.expensemanager.repository;

import com.ubs.expensemanager.domain.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.Optional;


@Repository
public interface DepartmentRepository extends JpaRepository<Department, UUID> {

    Optional<Department> findByName (String name);
}
