package com.ubs.expensemanager.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ubs.expensemanager.domain.Category;
import com.ubs.expensemanager.repository.CategoryRepository;

@Service
public class CategoryService {
    private final CategoryRepository repository;

    public CategoryService(CategoryRepository repository){
        this.repository = repository;
    }

    public Category createCategory (Category category){
        validateCategory(category);
        return category;
    }

    public List<Category> findAll(){
        return repository.findAll();
    }

    private void validateCategory (Category category){
        //validacao - work in progress
    }
}
