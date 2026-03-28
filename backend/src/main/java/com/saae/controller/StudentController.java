package com.saae.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.saae.model.Mark;
import com.saae.model.Student;
import com.saae.service.StudentService;

@RestController
@RequestMapping("/api/student")
@CrossOrigin(origins = "*")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @GetMapping("/profile/{userId}")
    public ResponseEntity<Student> getProfile(@PathVariable Long userId) {
        return studentService.getProfile(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/results/{studentId}")
    public ResponseEntity<List<Mark>> getMyResults(@PathVariable Long studentId) {
        return ResponseEntity.ok(studentService.getMyResults(studentId));
    }

    @GetMapping("/analysis/average/{studentId}")
    public ResponseEntity<Double> getMyAverage(@PathVariable Long studentId) {
        return ResponseEntity.ok(studentService.getMyAverage(studentId));
    }
}
