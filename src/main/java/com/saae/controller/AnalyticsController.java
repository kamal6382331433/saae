package com.saae.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.saae.service.AnalyticsService;

/**
 * Analytics Controller – covers Features 2 through 6.
 *
 * Base path: /analytics
 *
 * Endpoints:
 * GET /analytics/performance-trend/{studentId} → Feature 2: Semester trend
 * GET /analytics/weak-subjects[?limit=N] → Feature 3: Weak subject detection
 * GET /analytics/top-performers[?top=N] → Feature 4: Top performers
 * GET /analytics/risk-students[?avgThreshold=X&minFails=Y] → Feature 5: At-risk
 * students
 * GET /analytics/department-performance → Feature 6: Dept performance
 */
@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = "*")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    // -----------------------------------------------------------------------
    // FEATURE 2 – Semester Performance Trend
    // GET /analytics/performance-trend/{studentId}
    // -----------------------------------------------------------------------
    @GetMapping("/performance-trend/{studentId}")
    public ResponseEntity<Map<String, Object>> getPerformanceTrend(@PathVariable Long studentId) {
        Map<String, Object> result = analyticsService.getPerformanceTrend(studentId);
        return ResponseEntity.ok(result);
    }

    // -----------------------------------------------------------------------
    // FEATURE 3 – Weak Subject Detection
    // GET /analytics/weak-subjects?limit=5 (default: 5)
    // -----------------------------------------------------------------------
    @GetMapping("/weak-subjects")
    public ResponseEntity<List<Map<String, Object>>> getWeakSubjects(
            @RequestParam(defaultValue = "5") int limit) {
        List<Map<String, Object>> result = analyticsService.getWeakSubjects(limit);
        return ResponseEntity.ok(result);
    }

    // -----------------------------------------------------------------------
    // FEATURE 4 – Top Performer Identification
    // GET /analytics/top-performers?top=5 (default: 5)
    // -----------------------------------------------------------------------
    @GetMapping("/top-performers")
    public ResponseEntity<List<Map<String, Object>>> getTopPerformers(
            @RequestParam(defaultValue = "5") int top) {
        List<Map<String, Object>> result = analyticsService.getTopPerformers(top);
        return ResponseEntity.ok(result);
    }

    // -----------------------------------------------------------------------
    // FEATURE 5 – At-Risk Student Detection
    // GET /analytics/risk-students?avgThreshold=40&minFails=2
    // -----------------------------------------------------------------------
    @GetMapping("/risk-students")
    public ResponseEntity<List<Map<String, Object>>> getAtRiskStudents(
            @RequestParam(defaultValue = "40") double avgThreshold,
            @RequestParam(defaultValue = "2") long minFails) {
        List<Map<String, Object>> result = analyticsService.getAtRiskStudents(avgThreshold, minFails);
        return ResponseEntity.ok(result);
    }

    // -----------------------------------------------------------------------
    // FEATURE 6 – Department Level Performance
    // GET /analytics/department-performance
    // -----------------------------------------------------------------------
    @GetMapping("/department-performance")
    public ResponseEntity<List<Map<String, Object>>> getDepartmentPerformance() {
        List<Map<String, Object>> result = analyticsService.getDepartmentPerformance();
        return ResponseEntity.ok(result);
    }
}
