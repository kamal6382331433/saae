package com.saae.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "subjects")
public class Subject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "subject_name", nullable = false)
    private String subjectName;

    @Column(name = "subject_code", unique = true, nullable = false)
    private String subjectCode;

    // Additional legacy name column
    @Column(name = "name")
    private String name;

    // Legacy column to prevent MySQL strict mode insertion errors on old schemas
    @Column(name = "code")
    private String code;

    @Column(name = "credit_hours")
    private int creditHours;

    // Legacy column 'credits' found in some database versions
    @Column(name = "credits")
    private int credits;

    private String semester;

    private String department;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
        this.name = subjectName; // Sync legacy name
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.subjectName = name; // Sync
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
        this.code = subjectCode; // Sync legacy code
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
        this.subjectCode = code; // Sync
    }

    public int getCreditHours() {
        return creditHours;
    }

    public void setCreditHours(int creditHours) {
        this.creditHours = creditHours;
        this.credits = creditHours; // Sync with legacy column
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
        this.creditHours = credits; // Sync
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}
