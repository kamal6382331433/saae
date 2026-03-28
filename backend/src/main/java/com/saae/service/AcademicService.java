package com.saae.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.saae.model.Mark;
import com.saae.model.Student;
import com.saae.model.Subject;
import com.saae.repository.MarkRepository;
import com.saae.repository.StudentRepository;
import com.saae.repository.SubjectRepository;

@Service
public class AcademicService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private MarkRepository markRepository;

    // Student Management
    public Student addStudent(Student student) {
        if (student.getUser() == null || student.getUser().getId() == null) {
            throw new IllegalArgumentException("Student must be associated with a valid User.");
        }
        return studentRepository.save(student);
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Optional<Student> getStudentByUserId(Long userId) {
        return studentRepository.findByUser_Id(userId);
    }

    public Optional<Student> getStudentByRollNumber(String rollNumber) {
        return studentRepository.findByRollNumber(rollNumber);
    }

    // Subject Management
    public Subject addSubject(Subject subject) {
        if (subject.getSubjectCode() == null || subject.getSubjectCode().isEmpty()) {
            throw new IllegalArgumentException("Subject code is required.");
        }
        return subjectRepository.save(subject);
    }

    public List<Subject> getAllSubjects() {
        return subjectRepository.findAll();
    }

    public Optional<Subject> getSubjectByCode(String code) {
        return subjectRepository.findBySubjectCode(code);
    }

    // Marks Management
    public Mark addMark(Mark mark) {
        if (mark.getStudent() == null || mark.getSubject() == null) {
            throw new IllegalArgumentException("Mark must be linked to both a student and a subject.");
        }
        return markRepository.save(mark);
    }

    public List<Mark> getMarksByStudentId(Long studentId) {
        return markRepository.findByStudent_Id(studentId);
    }

    public double calculateAverage(Long studentId) {
        List<Mark> marks = getMarksByStudentId(studentId);
        if (marks.isEmpty())
            return 0.0;

        double totalObtained = marks.stream().mapToDouble(Mark::getMarksObtained).sum();
        double totalMax = marks.stream().mapToDouble(Mark::getTotalMarks).sum();

        return totalMax > 0 ? (totalObtained / totalMax) * 100 : 0.0;
    }

    public List<Student> getWeakStudents() {
        // Threshold: Average < 40%
        return studentRepository.findAll().stream()
                .filter(student -> calculateAverage(student.getId()) < 40.0)
                .toList();
    }
}
