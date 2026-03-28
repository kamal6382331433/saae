-- ============================================================
-- SAAE Seed Data
-- Default users and sample data for initial setup
-- ============================================================
-- NOTE: Passwords are BCrypt hashed.
-- Default passwords:
--   admin    -> admin123
--   faculty  -> faculty123
--   student  -> student123
-- ============================================================

USE saae_db;

-- ============================================================
-- Default Users (BCrypt hashed passwords)
-- ============================================================
INSERT INTO users (username, password, role, full_name, email) VALUES
('admin',   '$2a$10$dXJ3SW6G7P50lGmMQgel2eGD/y5z5ptmv6Z8FOnz/OI0e66hS0YSC', 'ADMIN',   'System Administrator', 'admin@saae.edu'),
('faculty', '$2a$10$dXJ3SW6G7P50lGmMQgel2eGD/y5z5ptmv6Z8FOnz/OI0e66hS0YSC', 'FACULTY', 'Dr. Jane Smith',       'jane.smith@saae.edu'),
('student', '$2a$10$dXJ3SW6G7P50lGmMQgel2eGD/y5z5ptmv6Z8FOnz/OI0e66hS0YSC', 'STUDENT', 'John Doe',            'john.doe@saae.edu');

-- ============================================================
-- Sample Subjects
-- ============================================================
INSERT INTO subjects (subject_name, subject_code, name, code, credit_hours, credits, semester, department) VALUES
('Data Structures',         'CS101', 'Data Structures',         'CS101', 4, 4, '4', 'Computer Science'),
('Database Management',     'CS102', 'Database Management',     'CS102', 3, 3, '4', 'Computer Science'),
('Operating Systems',       'CS103', 'Operating Systems',       'CS103', 4, 4, '5', 'Computer Science'),
('Mathematics III',         'MA201', 'Mathematics III',         'MA201', 3, 3, '3', 'Mathematics'),
('Digital Electronics',     'EC101', 'Digital Electronics',     'EC101', 3, 3, '3', 'Electronics');

-- ============================================================
-- Sample Student
-- ============================================================
INSERT INTO students (roll_number, department, current_semester, name, user_id)
VALUES ('SAAE001', 'Computer Science', '4', 'John Doe', (SELECT id FROM users WHERE username = 'student'));

-- ============================================================
-- Sample Marks
-- ============================================================
INSERT INTO marks (student_id, subject_id, marks_obtained, total_marks, semester) VALUES
((SELECT id FROM students WHERE roll_number = 'SAAE001'), (SELECT id FROM subjects WHERE subject_code = 'CS101'), 85, 100, '4'),
((SELECT id FROM students WHERE roll_number = 'SAAE001'), (SELECT id FROM subjects WHERE subject_code = 'CS102'), 78, 100, '4'),
((SELECT id FROM students WHERE roll_number = 'SAAE001'), (SELECT id FROM subjects WHERE subject_code = 'MA201'), 92, 100, '3');
