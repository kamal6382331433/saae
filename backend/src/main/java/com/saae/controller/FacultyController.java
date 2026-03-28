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
import com.saae.service.FacultyService;

@RestController
@RequestMapping("/api/faculty")
@CrossOrigin(origins = "*")
public class FacultyController {

    @Autowired
    private FacultyService facultyService;

    @PostMapping("/marks")
    public ResponseEntity<Mark> enterMark(@RequestBody Mark mark) {
        return ResponseEntity.ok(facultyService.saveMark(mark));
    }

    @GetMapping("/marks/subject/{subjectId}")
    public ResponseEntity<List<Mark>> getSubjectMarks(@PathVariable Long subjectId) {
        return ResponseEntity.ok(facultyService.getMarksBySubject(subjectId));
    }

    @GetMapping("/students")
    public ResponseEntity<List<Student>> getStudents() {
        return ResponseEntity.ok(facultyService.getAllStudents());
    }

    @GetMapping("/analysis/weak-students")
    public ResponseEntity<List<Student>> getWeakStudents() {
        return ResponseEntity.ok(facultyService.getWeakStudents());
    }
}
