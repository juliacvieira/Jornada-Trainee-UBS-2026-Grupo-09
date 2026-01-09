package com.ubs.expensemanager.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.ubs.expensemanager.domain.Category;
import com.ubs.expensemanager.dto.CreateCategoryRequest;
import com.ubs.expensemanager.dto.UpdateCategoryRequest;
import com.ubs.expensemanager.repository.CategoryRepository;

@Service
public class CategoryService {
    private final CategoryRepository repository;

    public CategoryService(CategoryRepository repository){
        this.repository = repository;
    }

    public Category createCategory (CreateCategoryRequest request){
        Category category = new Category();

        category.setName(request.name());
        category.setDailyLimit(request.dailyLimit());
        category.setMonthlyLimit(request.monthlyLimit());
        
        boolean valid = validateCategory(category);

        try {
            if (valid == true) {
            Category saved = repository.save(category);
            return saved;
        }
        } catch (Exception e) {
            System.out.println("Validation exception: " + e);
        }

        return category;
    }

    public List<Category> findAll(){
        return repository.findAll();
    }

    public Category updateCategory (UUID id, UpdateCategoryRequest request){
        Category category = findById(id);

        //acrescentar validação
        category.setName(request.name());
        category.setDailyLimit(request.dailyLimit());
        category.setMonthlyLimit(request.monthlyLimit());

        return category;

    }

    public Category findById(UUID id){
        return repository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("Category" + id + "not found"));
    }

    private boolean validateCategory (Category category){
        //validacao - work in progress

        return true;
    }
}
