DELETE FROM users
WHERE email IN (
  'admin@ubs.com',
  'manager@ubs.com',
  'employee@ubs.com'
);

INSERT INTO users (id, email, password, name, role, active, created_at, updated_at)
VALUES
  ('550e8400-e29b-41d4-a716-446655440002', 'manager@ubs.com',
   '$2b$12$W79LwmL3f8IMqMIxmPU9rukKde4ha03Vnq77QB/HGUTaaOm61MW6m',
   'Manager User', 'MANAGER', true, now(), now()),
  ('550e8400-e29b-41d4-a716-446655440003', 'employee@ubs.com',
   '$2b$12$NxIbwOwBUqLEHS5SwZMtNehHuenZA88HK.4srt4/MxfjJPXaT0sja',
   'Employee User', 'EMPLOYEE', true, now(), now());