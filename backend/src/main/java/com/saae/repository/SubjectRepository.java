package com.saae.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.saae.model.Subject;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
    Optional<Subject> findBySubjectCode(String subjectCode);
}
