package com.saae.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.saae.model.Student;

public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByUser_Id(Long userId);

    Optional<Student> findByRollNumber(String rollNumber);

    List<Student> findByDepartment(String department);

    // Students who scored above a certain average percentage
    @Query("SELECT DISTINCT m.student FROM Mark m " +
            "GROUP BY m.student " +
            "HAVING AVG(m.marksObtained / m.totalMarks * 100) >= :minAvg")
    List<Student> findStudentsAboveAveragePercent(@Param("minAvg") double minAvg);
}
