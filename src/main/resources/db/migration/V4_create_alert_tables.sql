-- Alerts table: linked to expenses
CREATE TABLE IF NOT EXISTS alerts (
    id UUID PRIMARY KEY,
    expense_id UUID NOT NULL,
    type VARCHAR(50) NOT NULL,
    message VARCHAR(500) NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_alerts_expense
    FOREIGN KEY (expense_id) REFERENCES expenses(id)
    );

CREATE INDEX IF NOT EXISTS idx_alerts_status ON alerts(status);
CREATE INDEX IF NOT EXISTS idx_alerts_expense_id ON alerts(expense_id);