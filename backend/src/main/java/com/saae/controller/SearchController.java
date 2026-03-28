package com.saae.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.saae.service.SearchService;

/**
 * FEATURE 1 – Advanced Student Search Controller
 *
 * Endpoint: GET /search/students
 *
 * Optional Query Parameters:
 * minMarks – students who scored below this raw mark in any subject
 * subject – subject name
 * status – "fail" or "pass" (requires subject)
 * minAvgPercent – students whose overall average >= this value (0-100)
 * department – filter by department name
 * maxAvgPercent – used with department: students from dept with avg < this
 * value
 *
 * Examples:
 * GET /search/students?minMarks=40
 * GET /search/students?subject=Maths&status=fail
 * GET /search/students?minAvgPercent=75
 * GET /search/students?department=CS&maxAvgPercent=50
 * GET /search/students?department=CS
 */
@RestController
@RequestMapping("/api/search")
@CrossOrigin(origins = "*")
public class SearchController {

    @Autowired
    private SearchService searchService;

    @GetMapping("/students")
    public ResponseEntity<List<Map<String, Object>>> searchStudents(
            @RequestParam(required = false) Double minMarks,
            @RequestParam(required = false) String subject,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Double minAvgPercent,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) Double maxAvgPercent) {

        List<Map<String, Object>> result = searchService.searchStudents(
                minMarks, subject, status, minAvgPercent, department, maxAvgPercent);

        return ResponseEntity.ok(result);
    }
}
