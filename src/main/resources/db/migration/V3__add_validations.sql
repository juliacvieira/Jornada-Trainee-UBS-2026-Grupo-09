--Adding validations/constraints to columns with monetary values
ALTER TABLE departments
ADD CONSTRAINT chk_department_monthly_budget
CHECK (monthly_budget > 0); 

ALTER TABLE categories
ADD CONSTRAINT chk_categories_daily_limit
CHECK (daily_limit >= 0);

ALTER TABLE categories
ADD CONSTRAINT chk_categories_monthly_limit
CHECK (monthly_limit >= 0);

ALTER TABLE expenses
ADD CONSTRAINT chk_expenses_amount
CHECK (amount > 0);