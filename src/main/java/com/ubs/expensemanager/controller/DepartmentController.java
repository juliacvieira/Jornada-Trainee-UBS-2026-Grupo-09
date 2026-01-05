package com.ubs.expensemanager.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/department")
public class DepartmentController {

    @GetMapping
    public String getDepartment(){
        return "teste";
    }

    @PostMapping
    public String newDepartment(){
        return "teste";
    }
}
