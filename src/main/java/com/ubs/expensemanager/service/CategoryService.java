package com.ubs.expensemanager.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ubs.expensemanager.domain.Category;
import com.ubs.expensemanager.dto.category.CreateCategoryRequest;
import com.ubs.expensemanager.dto.category.UpdateCategoryRequest;
import com.ubs.expensemanager.repository.CategoryRepository;

@Service
public class CategoryService {
    private final CategoryRepository repository;

    public CategoryService(CategoryRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Category createCategory (CreateCategoryRequest request) {
        Category category = new Category();

        category.setName(request.name());
        category.setDailyLimit(request.dailyLimit());
        category.setMonthlyLimit(request.monthlyLimit());
        
        validateCategory(category);
        return repository.save(category);
    }

    public List<Category> findAll() {
        return repository.findAll();
    }

    @Transactional
    public Category updateCategory(UUID id, UpdateCategoryRequest request) {
        Category category = findById(id);

        if (request.name() != null){
            category.setName(request.name());
        }
        if (request.dailyLimit() != null){
            category.setDailyLimit(request.dailyLimit());
        }
        if (request.monthlyLimit() != null){
            category.setMonthlyLimit(request.monthlyLimit());
        }
        validateCategory(category);
        return repository.save(category);

    }

    public Category findById(UUID id) {
        return repository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("Category" + id + "not found"));
    }

    private boolean validateCategory(Category category) {

    	if (category.getDailyLimit() == null || category.getMonthlyLimit() == null) {
            throw new IllegalArgumentException("Limits must be provided");
        }
        if (category.getDailyLimit().compareTo(BigDecimal.ZERO) < 0 ||
            category.getMonthlyLimit().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Limits must be non-negative");
        }
        if (category.getMonthlyLimit().compareTo(category.getDailyLimit()) < 0) {
            throw new IllegalArgumentException("Monthly limit must be >= daily limit");
        }

        return true;
    }
}
