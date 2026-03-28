-- ============================================================
-- SAAE Database Schema
-- Students Academic Analysis Engine
-- ============================================================
-- Database: saae_db (MySQL)
-- Generated from JPA Entity Models
-- ============================================================

CREATE DATABASE IF NOT EXISTS saae_db;
USE saae_db;

-- ============================================================
-- TABLE: users
-- Stores all user accounts (Admin, Faculty, Student)
-- ============================================================
CREATE TABLE IF NOT EXISTS users (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(255) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    role        VARCHAR(255) NOT NULL COMMENT 'ADMIN, FACULTY, STUDENT',
    full_name   VARCHAR(255),
    email       VARCHAR(255) UNIQUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- TABLE: students
-- Stores student profile linked to a user account
-- ============================================================
CREATE TABLE IF NOT EXISTS students (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    roll_number      VARCHAR(255) NOT NULL UNIQUE,
    department       VARCHAR(255),
    current_semester VARCHAR(255),
    name             VARCHAR(255),
    user_id          BIGINT NOT NULL,
    CONSTRAINT fk_student_user FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- TABLE: subjects
-- Stores academic subjects/courses
-- ============================================================
CREATE TABLE IF NOT EXISTS subjects (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    subject_name VARCHAR(255) NOT NULL,
    subject_code VARCHAR(255) NOT NULL UNIQUE,
    name         VARCHAR(255) COMMENT 'Legacy column',
    code         VARCHAR(255) COMMENT 'Legacy column',
    credit_hours INT,
    credits      INT COMMENT 'Legacy column',
    semester     VARCHAR(255),
    department   VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- TABLE: marks
-- Stores student marks per subject
-- ============================================================
CREATE TABLE IF NOT EXISTS marks (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id     BIGINT NOT NULL,
    subject_id     BIGINT NOT NULL,
    marks_obtained DOUBLE,
    total_marks    DOUBLE,
    semester       VARCHAR(255),
    CONSTRAINT fk_mark_student FOREIGN KEY (student_id) REFERENCES students(id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_mark_subject FOREIGN KEY (subject_id) REFERENCES subjects(id)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
