package com.ubs.expensemanager.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/category")
public class CategoryController {

    @GetMapping
    public String getCategory(){
        return "teste";
    }

    @PostMapping
    public String newCategory(){
        return "teste";
    }
}
