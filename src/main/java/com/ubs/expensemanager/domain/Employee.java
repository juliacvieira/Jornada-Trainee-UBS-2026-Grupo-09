package com.ubs.expensemanager.domain;

import jakarta.persistence.*;
import java.util.UUID;

import com.ubs.expensemanager.domain.enums.EmployeeRole;

@Entity
@Table(name = "employees")
public class Employee {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    private String position;

    @ManyToOne
    @JoinColumn(name = "manager_id")
    private Employee manager;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;
    
    @Enumerated(EnumType.STRING)
    private EmployeeRole role;

    public Employee() {}

   

    // getters / setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }
    public Employee getManager() { return manager; }
    public void setManager(Employee manager) { this.manager = manager; }
    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }
    public EmployeeRole getRole() {return role;}
    public void setRole(EmployeeRole role) {this.role = role;}
}
