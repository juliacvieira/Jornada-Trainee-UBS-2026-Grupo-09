package com.ubs.expensemanager.repository;

import com.ubs.expensemanager.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
}
