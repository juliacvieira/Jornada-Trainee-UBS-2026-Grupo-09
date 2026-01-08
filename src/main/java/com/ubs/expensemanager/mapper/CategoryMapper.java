package com.ubs.expensemanager.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.ubs.expensemanager.domain.Category;
import com.ubs.expensemanager.dto.CategoryResponse;

@Component
public class CategoryMapper {

    public CategoryResponse toResponse (Category category){
        return new CategoryResponse(
            category.getId(),
            category.getName(),
            category.getDailyLimit(),
            category.getMonthlyLimit()
        );
    }

    public List<CategoryResponse> toResponseList (List<Category> categorys){
        return categorys.stream()
                .map(category -> toResponse(category))
                .toList();
    }
}
