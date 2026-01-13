-- Insert additional expenses from frontend mock data
INSERT INTO
    expenses (
        id,
        employee_id,
        category_id,
        currency,
        amount,
        date,
        description,
        status
    )
VALUES
    -- ExpensesPage mock data - João Silva expenses
    (
        '10000000-0000-0000-0000-000000000001',
        '00000000-0000-0000-0000-000000000001',
        '00000000-0000-0000-0000-000000000001',
        'BRL',
        2500.00,
        '2025-12-20',
        'Trip to conference in São Paulo',
        'APPROVED_MANAGER'
    ),
    (
        '10000000-0000-0000-0000-000000000002',
        '00000000-0000-0000-0000-000000000001',
        '00000000-0000-0000-0000-000000000002',
        'BRL',
        85.50,
        '2025-12-22',
        'Lunch with client',
        'PENDING'
    ),
    (
        '10000000-0000-0000-0000-000000000003',
        '00000000-0000-0000-0000-000000000001',
        '00000000-0000-0000-0000-000000000003',
        'BRL',
        45.00,
        '2025-12-23',
        'Uber for meeting',
        'APPROVED_MANAGER'
    ),
    (
        '10000000-0000-0000-0000-000000000004',
        '00000000-0000-0000-0000-000000000001',
        '00000000-0000-0000-0000-000000000002',
        'BRL',
        120.00,
        '2025-12-24',
        'Dinner with the team',
        'REJECTED'
    ),
    (
        '10000000-0000-0000-0000-000000000005',
        '00000000-0000-0000-0000-000000000001',
        '00000000-0000-0000-0000-000000000001',
        'BRL',
        3500.00,
        '2025-12-18',
        'International flight',
        'REJECTED'
    ),

-- ApprovalPage mock pending expenses - João Santos
(
    '20000000-0000-0000-0000-000000000001',
    '00000000-0000-0000-0000-000000000001',
    '00000000-0000-0000-0000-000000000001',
    'BRL',
    3200.00,
    '2025-12-20',
    'Airfare to São Paulo - Technology Conference',
    'PENDING'
),

-- ApprovalPage mock pending expenses - Mariana Souza (using employee ID 2)
(
    '20000000-0000-0000-0000-000000000002',
    '00000000-0000-0000-0000-000000000002',
    '00000000-0000-0000-0000-000000000002',
    'BRL',
    180.50,
    '2025-12-22',
    'Dinner with potential client - Project discussion',
    'PENDING'
),

-- ApprovalPage mock pending expenses - Pedro Lima (using employee ID 5)
(
    '20000000-0000-0000-0000-000000000003',
    '00000000-0000-0000-0000-000000000005',
    '00000000-0000-0000-0000-000000000003',
    'BRL',
    95.00,
    '2025-12-23',
    'Taxi for urgent meeting with client',
    'PENDING'
),

-- ApprovalPage mock approved expenses - João Santos
(
    '20000000-0000-0000-0000-000000000004',
    '00000000-0000-0000-0000-000000000001',
    '00000000-0000-0000-0000-000000000002',
    'BRL',
    65.00,
    '2025-12-15',
    'Executive lunch',
    'APPROVED_FINANCE'
),

-- ApprovalPage mock approved expenses - Mariana Souza
(
    '20000000-0000-0000-0000-000000000005',
    '00000000-0000-0000-0000-000000000002',
    '00000000-0000-0000-0000-000000000003',
    'BRL',
    45.00,
    '2025-12-18',
    'Uber for client office',
    'APPROVED_FINANCE'
),

-- ApprovalPage mock rejected expenses - Pedro Lima
(
    '20000000-0000-0000-0000-000000000006',
    '00000000-0000-0000-0000-000000000005',
    '00000000-0000-0000-0000-000000000002',
    'BRL',
    250.00,
    '2025-12-16',
    'Dinner at the restaurant',
    'REJECTED'
);