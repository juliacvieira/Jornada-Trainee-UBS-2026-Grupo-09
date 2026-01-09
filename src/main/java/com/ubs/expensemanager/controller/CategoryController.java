package com.ubs.expensemanager.controller;

import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ubs.expensemanager.domain.Category;
import com.ubs.expensemanager.dto.category.CategoryResponse;
import com.ubs.expensemanager.dto.category.CreateCategoryRequest;
import com.ubs.expensemanager.dto.category.UpdateCategoryRequest;
import com.ubs.expensemanager.mapper.CategoryMapper;
import com.ubs.expensemanager.service.CategoryService;

@RestController
@RequestMapping("/category")
public class CategoryController {

    private final CategoryService service;
    private final CategoryMapper mapper;

    public CategoryController (CategoryService service, CategoryMapper mapper){
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping("/{id}")
    public CategoryResponse getCategory(@PathVariable UUID id){
        Category category = service.findById(id);
        return mapper.toResponse(category); 
    }

    @PostMapping
    public CategoryResponse newCategory(@RequestBody CreateCategoryRequest request){
        Category category = service.createCategory(request);
        return mapper.toResponse(category);
    }

    @PatchMapping("/{id}")
    public CategoryResponse updateCategory (@PathVariable UUID id, @RequestBody UpdateCategoryRequest request){
        Category category = service.updateCategory(id, request);
        return mapper.toResponse(category);
    }
}
