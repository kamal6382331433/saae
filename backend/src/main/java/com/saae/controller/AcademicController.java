package com.saae.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.saae.model.Mark;
import com.saae.model.Student;
import com.saae.model.Subject;
import com.saae.service.AcademicService;

@RestController
@RequestMapping("/api/academic")
@CrossOrigin(origins = "*")
public class AcademicController {

    @Autowired
    private AcademicService academicService;

    // Student Endpoints
    @PostMapping("/students")
    public ResponseEntity<Student> addStudent(@RequestBody Student student) {
        return ResponseEntity.ok(academicService.addStudent(student));
    }

    @GetMapping("/students")
    public ResponseEntity<List<Student>> getAllStudents() {
        return ResponseEntity.ok(academicService.getAllStudents());
    }

    @GetMapping("/students/user/{userId}")
    public ResponseEntity<Student> getStudentByUserId(@PathVariable Long userId) {
        return academicService.getStudentByUserId(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Subject Endpoints
    @PostMapping("/subjects")
    public ResponseEntity<Subject> addSubject(@RequestBody Subject subject) {
        return ResponseEntity.ok(academicService.addSubject(subject));
    }

    @GetMapping("/subjects")
    public ResponseEntity<List<Subject>> getAllSubjects() {
        return ResponseEntity.ok(academicService.getAllSubjects());
    }

    // Marks Endpoints
    @PostMapping("/marks")
    public ResponseEntity<Mark> addMark(@RequestBody Mark mark) {
        return ResponseEntity.ok(academicService.addMark(mark));
    }

    @GetMapping("/marks/student/{studentId}")
    public ResponseEntity<List<Mark>> getMarksByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(academicService.getMarksByStudentId(studentId));
    }

    @GetMapping("/analysis/average/{studentId}")
    public ResponseEntity<Double> getStudentAverage(@PathVariable Long studentId) {
        return ResponseEntity.ok(academicService.calculateAverage(studentId));
    }

    @GetMapping("/analysis/weak-students")
    public ResponseEntity<List<Student>> getWeakStudents() {
        return ResponseEntity.ok(academicService.getWeakStudents());
    }
}
