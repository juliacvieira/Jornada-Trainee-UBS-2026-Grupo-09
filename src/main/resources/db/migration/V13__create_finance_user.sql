DELETE FROM users
WHERE email IN (
  'finance@ubs.com'
);

INSERT INTO users (id, email, password, name, role, active, created_at, updated_at)
VALUES
  ('550e8400-e29b-41d4-a716-446655440007', 'finance@ubs.com',
   '$2a$12$4vPYPXeCuAp70bgZGa1gUed/CBlqdwGyU/ZyFTbdupHSmCeuTc4L.',
   'Finance User', 'FINANCE', true, now(), now());