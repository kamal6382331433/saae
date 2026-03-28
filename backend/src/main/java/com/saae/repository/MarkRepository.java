package com.saae.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.saae.model.Mark;

public interface MarkRepository extends JpaRepository<Mark, Long> {

        List<Mark> findByStudent_Id(Long studentId);

        List<Mark> findBySubject_Id(Long subjectId);

        // All marks for a given subject name (case-insensitive)
        @Query("SELECT m FROM Mark m WHERE LOWER(m.subject.subjectName) = LOWER(:subjectName)")
        List<Mark> findBySubjectName(@Param("subjectName") String subjectName);

        // Students who scored below a threshold in any mark entry
        @Query("SELECT DISTINCT m.student FROM Mark m WHERE m.marksObtained < :minMarks")
        List<com.saae.model.Student> findStudentsBelowMarks(@Param("minMarks") double minMarks);

        // Students who failed (< 40% in a specific subject)
        @Query("SELECT DISTINCT m.student FROM Mark m " +
                        "WHERE LOWER(m.subject.subjectName) = LOWER(:subjectName) " +
                        "AND (m.marksObtained / m.totalMarks) * 100 < 40.0")
        List<com.saae.model.Student> findStudentsFailingSubject(@Param("subjectName") String subjectName);

        // Students from a department who scored below average threshold
        @Query("SELECT DISTINCT m.student FROM Mark m " +
                        "WHERE m.student.department = :department " +
                        "GROUP BY m.student " +
                        "HAVING AVG(m.marksObtained / m.totalMarks * 100) < :maxAvg")
        List<com.saae.model.Student> findWeakStudentsByDepartment(
                        @Param("department") String department,
                        @Param("maxAvg") double maxAvg);

        // Average marks per subject (for weak subject detection)
        @Query("SELECT m.subject.subjectName, AVG(m.marksObtained / m.totalMarks * 100) " +
                        "FROM Mark m GROUP BY m.subject.subjectName ORDER BY AVG(m.marksObtained / m.totalMarks * 100) ASC")
        List<Object[]> findAverageMarksBySubject();

        // Top performers: student id + average
        @Query("SELECT m.student, AVG(m.marksObtained / m.totalMarks * 100) " +
                        "FROM Mark m GROUP BY m.student ORDER BY AVG(m.marksObtained / m.totalMarks * 100) DESC")
        List<Object[]> findTopPerformers();

        // At-risk students: average < threshold
        @Query("SELECT m.student, AVG(m.marksObtained / m.totalMarks * 100) " +
                        "FROM Mark m GROUP BY m.student HAVING AVG(m.marksObtained / m.totalMarks * 100) < :threshold ORDER BY AVG(m.marksObtained / m.totalMarks * 100) ASC")
        List<Object[]> findAtRiskStudents(@Param("threshold") double threshold);

        // Count of failed subjects per student
        @Query("SELECT m.student, COUNT(m) " +
                        "FROM Mark m WHERE (m.marksObtained / m.totalMarks * 100) < 40.0 " +
                        "GROUP BY m.student HAVING COUNT(m) >= :minFails")
        List<Object[]> findStudentsWithMultipleFails(@Param("minFails") long minFails);

        // Department average performance
        @Query("SELECT m.student.department, AVG(m.marksObtained / m.totalMarks * 100) " +
                        "FROM Mark m GROUP BY m.student.department ORDER BY AVG(m.marksObtained / m.totalMarks * 100) DESC")
        List<Object[]> findAverageMarksByDepartment();

        // Department toppers
        @Query("SELECT m.student.department, m.student, AVG(m.marksObtained / m.totalMarks * 100) " +
                        "FROM Mark m GROUP BY m.student.department, m.student ORDER BY m.student.department, AVG(m.marksObtained / m.totalMarks * 100) DESC")
        List<Object[]> findDepartmentToppers();

        // Semester-wise averages for a student
        @Query("SELECT m.semester, AVG(m.marksObtained / m.totalMarks * 100) " +
                        "FROM Mark m WHERE m.student.id = :studentId " +
                        "GROUP BY m.semester ORDER BY m.semester ASC")
        List<Object[]> findSemesterAveragesForStudent(@Param("studentId") Long studentId);
}
