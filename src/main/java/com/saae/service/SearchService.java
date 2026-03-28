package com.saae.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.saae.model.Mark;
import com.saae.model.Student;
import com.saae.repository.MarkRepository;
import com.saae.repository.StudentRepository;

/**
 * FEATURE 1 – Advanced Student Search
 *
 * Supports filtering students by:
 * - minMarks : students who scored below this value in at least one mark entry
 * - subject + status: students failing/passing a particular subject
 * - minAvgPercent : students whose overall average is above this threshold
 * - department + maxAvg : students from a department whose average is below
 * maxAvg
 */
@Service
public class SearchService {

    @Autowired
    private MarkRepository markRepository;

    @Autowired
    private StudentRepository studentRepository;

    /**
     * Search students with optional filters.
     *
     * @param minMarks      (optional) raw marks threshold – returns students with
     *                      any mark < minMarks
     * @param subject       (optional) subject name to check when combined with
     *                      status
     * @param status        (optional) "fail" or "pass" – used together with subject
     * @param minAvgPercent (optional) returns students whose overall average >=
     *                      minAvgPercent
     * @param department    (optional) filters students from a specific department
     * @param maxAvgPercent (optional) used with department – returns students whose
     *                      dept average < maxAvgPercent
     */
    public List<Map<String, Object>> searchStudents(
            Double minMarks,
            String subject,
            String status,
            Double minAvgPercent,
            String department,
            Double maxAvgPercent) {

        List<Student> result = new ArrayList<>();

        // Filter: students scoring below minMarks in any mark entry
        if (minMarks != null) {
            result = markRepository.findStudentsBelowMarks(minMarks);
        }

        // Filter: students failing/passing a specific subject
        else if (subject != null && "fail".equalsIgnoreCase(status)) {
            result = markRepository.findStudentsFailingSubject(subject);
        } else if (subject != null && "pass".equalsIgnoreCase(status)) {
            // pass = not failing
            List<Student> failing = markRepository.findStudentsFailingSubject(subject);
            List<Long> failingIds = failing.stream().map(Student::getId).collect(Collectors.toList());
            result = markRepository.findBySubjectName(subject).stream()
                    .map(Mark::getStudent)
                    .distinct()
                    .filter(s -> !failingIds.contains(s.getId()))
                    .collect(Collectors.toList());
        }

        // Filter: students above a certain average percentage
        else if (minAvgPercent != null) {
            result = studentRepository.findStudentsAboveAveragePercent(minAvgPercent);
        }

        // Filter: students from a department with low average
        else if (department != null && maxAvgPercent != null) {
            result = markRepository.findWeakStudentsByDepartment(department, maxAvgPercent);
        }

        // Filter: students from a department only
        else if (department != null) {
            result = studentRepository.findByDepartment(department);
        }

        // No filter: return all students
        else {
            result = studentRepository.findAll();
        }

        return buildStudentResponseList(result);
    }

    private List<Map<String, Object>> buildStudentResponseList(List<Student> students) {
        return students.stream().map(s -> {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", s.getId());
            map.put("rollNumber", s.getRollNumber());
            map.put("fullName", s.getUser() != null ? s.getUser().getFullName() : null);
            map.put("email", s.getUser() != null ? s.getUser().getEmail() : null);
            map.put("department", s.getDepartment());
            map.put("semester", s.getCurrentSemester());
            return map;
        }).collect(Collectors.toList());
    }
}
