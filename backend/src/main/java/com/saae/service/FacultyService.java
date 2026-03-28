package com.saae.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.saae.model.Mark;
import com.saae.model.Student;
import com.saae.model.Subject;
import com.saae.repository.MarkRepository;
import com.saae.repository.StudentRepository;
import com.saae.repository.SubjectRepository;

@Service
public class FacultyService {

    @Autowired
    private MarkRepository markRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private AcademicService academicService;

    // Mark Entry
    public Mark saveMark(Mark mark) {
        return markRepository.save(mark);
    }

    public List<Mark> getMarksBySubject(Long subjectId) {
        // This is a simplified version, in real SRS it might need semester filtering
        return markRepository.findAll().stream()
                .filter(m -> m.getSubject().getId().equals(subjectId))
                .toList();
    }

    // Student Analysis for Faculty
    public List<Student> getWeakStudents() {
        return academicService.getWeakStudents();
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public List<Subject> getSubjectsByDepartment(String dept) {
        return subjectRepository.findAll().stream()
                .filter(s -> s.getDepartment().equalsIgnoreCase(dept))
                .toList();
    }
}
