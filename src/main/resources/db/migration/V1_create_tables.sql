--Creating Enum Types
CREATE TYPE expense_status AS ENUM ('PENDING', 'APPROVED', 'REJECTED');

-- Creating Tables 
CREATE TABLE departments (
    id UUID PRIMARY KEY,s

    name VARCHAR(100) NOT NULL,
    monthly_budget DECIMAL(19, 4) NOT NULL
);

CREATE TABLE categories (
    id UUID PRIMARY KEY,

    name VARCHAR(100) NOT NULL,
    daily_limit DECIMAL(19, 4) NOT NULL,
    monthly_limit DECIMAL(19, 4) NOT NULL
);

CREATE TABLE expenses (
    id UUID PRIMARY KEY,

    employee_id UUID NOT NULL,
    category_id UUID NOT NULL,

    currency VARCHAR(10) NOT NULL,
    amount DECIMAL(19, 4) NOT NULL,
    date DATE NOT NULL,
    description VARCHAR(255),
    status VARCHAR(50) NOT NULL,

    CONSTRAINT fk_expense_employee
        FOREIGN KEY (employee_id) REFERENCES employees(id),
    CONSTRAINT fk_expense_category
        FOREIGN KEY (category_id) REFERENCES categories(id)
);

CREATE TABLE employees (
    id UUID PRIMARY KEY,

    name VARCHAR(100) NOT NULL,

    department_id UUID,
    manager_id UUID,

    email VARCHAR(100) NOT NULL UNIQUE,
    position VARCHAR(100),
    
    CONSTRAINT fk_employee_department
        FOREIGN KEY (department_id) REFERENCES departments(id),
    CONSTRAINT fk_employee_manager
        FOREIGN KEY (manager_id) REFERENCES employees(id)
);

-- Creating Indexes
CREATE INDEX idx_expenses_employee_id ON expenses(employee_id);
CREATE INDEX idx_expenses_category_id ON expenses(category_id);
CREATE INDEX idx_employees_department_id ON employees(department_id);
CREATE INDEX idx_employees_manager_id ON employees(manager_id);
