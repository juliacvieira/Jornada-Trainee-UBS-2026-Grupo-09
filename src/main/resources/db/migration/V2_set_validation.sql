-- Creating new ENUM to include "APPROVED_MANAGER" and "APPROVED_FINANCE"
CREATE TYPE status_expense AS ENUM ('PENDING', 
                                    'APPROVED_MANAGER', 
                                    'APPROVED_FINANCE', 
                                    'REJECTED');

--Currently there is no data yet in our DB, but for future scenarios we have implemented this update
UPDATE expenses
SET status = 'APPROVED_MANAGER'
WHERE status = 'APPROVED';


-- Updating the table 'expenses' that used the old ENUM in the 'status' column
ALTER TABLE expenses
ALTER COLUMN status TYPE status_expense
USING status::text::status_expense;

-- Dropping old ENUM
DROP TYPE expense_status;