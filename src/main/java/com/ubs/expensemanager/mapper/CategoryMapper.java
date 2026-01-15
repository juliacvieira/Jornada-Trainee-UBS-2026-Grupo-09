package com.ubs.expensemanager.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.ubs.expensemanager.domain.Category;
import com.ubs.expensemanager.dto.category.CategoryResponse;

@Component
public class CategoryMapper {

	public static CategoryResponse toResponse(Category c) {
        return new CategoryResponse(
        		c.getId(),
        		c.getName(),
        		c.getDailyLimit(),
        		c.getMonthlyLimit()
    		);
    }

    public List<CategoryResponse> toResponseList (List<Category> categorys){
        return categorys.stream()
                .map(category -> toResponse(category))
                .toList();
    }
}
