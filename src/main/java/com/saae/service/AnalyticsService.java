package com.saae.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.saae.model.Student;
import com.saae.repository.MarkRepository;
import com.saae.repository.StudentRepository;

/**
 * AnalyticsService covers:
 * Feature 2 – Semester Performance Trend
 * Feature 3 – Weak Subject Detection
 * Feature 4 – Top Performer Identification
 * Feature 5 – At-Risk Student Detection
 * Feature 6 – Department-Level Performance
 */
@Service
public class AnalyticsService {

    @Autowired
    private MarkRepository markRepository;

    @Autowired
    private StudentRepository studentRepository;

    // -------------------------------------------------------------------------
    // FEATURE 2 – Semester Performance Trend
    // -------------------------------------------------------------------------

    /**
     * Returns the performance trend of a student across semesters.
     * Trend is determined by comparing the average of the last semester
     * against the second-to-last semester.
     *
     * Improving → last avg > previous avg by > 5 points
     * Declining → last avg < previous avg by > 5 points
     * Stable → difference within ±5 points
     */
    public Map<String, Object> getPerformanceTrend(Long studentId) {
        List<Object[]> semesterAverages = markRepository.findSemesterAveragesForStudent(studentId);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("studentId", studentId);

        // Enrich with student name if available
        studentRepository.findById(studentId).ifPresent(s -> {
            response.put("rollNumber", s.getRollNumber());
            if (s.getUser() != null)
                response.put("studentName", s.getUser().getFullName());
        });

        if (semesterAverages == null || semesterAverages.isEmpty()) {
            response.put("trend", "No data available");
            response.put("semesterAverages", List.of());
            return response;
        }

        // Build semester detail list
        List<Map<String, Object>> details = semesterAverages.stream().map(row -> {
            Map<String, Object> d = new LinkedHashMap<>();
            d.put("semester", row[0]);
            d.put("averagePercent", round((Double) row[1]));
            return d;
        }).collect(Collectors.toList());

        response.put("semesterAverages", details);

        if (semesterAverages.size() < 2) {
            response.put("trend", "Insufficient data (only 1 semester)");
            return response;
        }

        // Compare the last two semesters
        double prevAvg = (Double) semesterAverages.get(semesterAverages.size() - 2)[1];
        double lastAvg = (Double) semesterAverages.get(semesterAverages.size() - 1)[1];
        double diff = lastAvg - prevAvg;

        String trend;
        if (diff > 5.0)
            trend = "Improving";
        else if (diff < -5.0)
            trend = "Declining";
        else
            trend = "Stable";

        response.put("trend", trend);
        response.put("lastSemesterAvg", round(lastAvg));
        response.put("previousSemesterAvg", round(prevAvg));
        return response;
    }

    // -------------------------------------------------------------------------
    // FEATURE 3 – Weak Subject Detection
    // -------------------------------------------------------------------------

    /**
     * Returns subjects ordered by their overall average percentage (ascending).
     * The first entries are the weakest subjects.
     * Optionally limits to the bottom N subjects.
     */
    public List<Map<String, Object>> getWeakSubjects(int limit) {
        List<Object[]> rows = markRepository.findAverageMarksBySubject();

        return rows.stream()
                .limit(limit)
                .map(row -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("subjectName", row[0]);
                    m.put("averagePercent", round((Double) row[1]));
                    return m;
                })
                .collect(Collectors.toList());
    }

    // -------------------------------------------------------------------------
    // FEATURE 4 – Top Performer Identification
    // -------------------------------------------------------------------------

    /**
     * Returns the top N students with the highest average percentage.
     */
    public List<Map<String, Object>> getTopPerformers(int topN) {
        List<Object[]> rows = markRepository.findTopPerformers();

        return rows.stream()
                .limit(topN)
                .map(row -> {
                    Student s = (Student) row[0];
                    double avg = (Double) row[1];
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("studentId", s.getId());
                    m.put("rollNumber", s.getRollNumber());
                    m.put("fullName", s.getUser() != null ? s.getUser().getFullName() : null);
                    m.put("department", s.getDepartment());
                    m.put("semester", s.getCurrentSemester());
                    m.put("averagePercent", round(avg));
                    return m;
                })
                .collect(Collectors.toList());
    }

    // -------------------------------------------------------------------------
    // FEATURE 5 – At-Risk Student Detection
    // -------------------------------------------------------------------------

    /**
     * Detects students who are academically at risk.
     * Criteria:
     * 1. Overall average < avgThreshold (default 40%)
     * 2. OR number of failed subjects >= minFailedSubjects (default 2)
     *
     * Both lists are merged (union) and deduplicated.
     */
    public List<Map<String, Object>> getAtRiskStudents(double avgThreshold, long minFailedSubjects) {
        List<Object[]> lowAvgRows = markRepository.findAtRiskStudents(avgThreshold);
        List<Object[]> multiFailRows = markRepository.findStudentsWithMultipleFails(minFailedSubjects);

        // Build a map (id → response entry) to deduplicate
        Map<Long, Map<String, Object>> riskMap = new LinkedHashMap<>();

        for (Object[] row : lowAvgRows) {
            Student s = (Student) row[0];
            double avg = (Double) row[1];
            Map<String, Object> entry = buildRiskEntry(s, avg, List.of("Low Average"));
            riskMap.put(s.getId(), entry);
        }

        for (Object[] row : multiFailRows) {
            Student s = (Student) row[0];
            long failCount = (Long) row[1];
            riskMap.merge(s.getId(),
                    buildRiskEntry(s, null, List.of("Multiple Failures (" + failCount + " subjects)")),
                    (existing, newEntry) -> {
                        // Merge risk reasons
                        @SuppressWarnings("unchecked")
                        List<String> existingReasons = new ArrayList<>((List<String>) existing.get("riskReasons"));
                        @SuppressWarnings("unchecked")
                        List<String> newReasons = (List<String>) newEntry.get("riskReasons");
                        existingReasons.addAll(newReasons);
                        existing.put("riskReasons", existingReasons);
                        existing.put("failedSubjectsCount", failCount);
                        return existing;
                    });
        }

        return new ArrayList<>(riskMap.values());
    }

    private Map<String, Object> buildRiskEntry(Student s, Double avg, List<String> reasons) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("studentId", s.getId());
        m.put("rollNumber", s.getRollNumber());
        m.put("fullName", s.getUser() != null ? s.getUser().getFullName() : null);
        m.put("department", s.getDepartment());
        m.put("semester", s.getCurrentSemester());
        if (avg != null)
            m.put("averagePercent", round(avg));
        m.put("riskReasons", new ArrayList<>(reasons));
        return m;
    }

    // -------------------------------------------------------------------------
    // FEATURE 6 – Department Level Performance
    // -------------------------------------------------------------------------

    /**
     * Returns department-wise average marks and the top performer per department.
     */
    public List<Map<String, Object>> getDepartmentPerformance() {
        List<Object[]> deptAvgRows = markRepository.findAverageMarksByDepartment();
        List<Object[]> topperRows = markRepository.findDepartmentToppers();

        // Build topper map: department → first (best) student (sorted desc by avg in
        // query)
        Map<String, Map<String, Object>> topperByDept = new LinkedHashMap<>();
        for (Object[] row : topperRows) {
            String dept = (String) row[0];
            Student topper = (Student) row[1];
            double avg = (Double) row[2];
            if (!topperByDept.containsKey(dept)) {
                Map<String, Object> t = new LinkedHashMap<>();
                t.put("studentId", topper.getId());
                t.put("rollNumber", topper.getRollNumber());
                t.put("fullName", topper.getUser() != null ? topper.getUser().getFullName() : null);
                t.put("averagePercent", round(avg));
                topperByDept.put(dept, t);
            }
        }

        return deptAvgRows.stream().map(row -> {
            String dept = (String) row[0];
            double deptAvg = (Double) row[1];
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("department", dept);
            m.put("averagePercent", round(deptAvg));
            m.put("topper", topperByDept.getOrDefault(dept, new HashMap<>()));
            return m;
        }).collect(Collectors.toList());
    }

    // -------------------------------------------------------------------------
    // Utility
    // -------------------------------------------------------------------------

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
