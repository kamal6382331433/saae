package com.saae.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.saae.model.Mark;
import com.saae.model.Student;
import com.saae.repository.MarkRepository;
import com.saae.repository.StudentRepository;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private MarkRepository markRepository;

    @Autowired
    private AcademicService academicService;

    public Optional<Student> getProfile(Long userId) {
        return studentRepository.findByUser_Id(userId);
    }

    public List<Mark> getMyResults(Long studentId) {
        return markRepository.findByStudent_Id(studentId);
    }

    public double getMyAverage(Long studentId) {
        return academicService.calculateAverage(studentId);
    }
}
