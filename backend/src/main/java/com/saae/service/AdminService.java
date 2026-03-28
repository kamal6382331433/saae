package com.saae.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.saae.model.Student;
import com.saae.model.Subject;
import com.saae.model.User;
import com.saae.repository.StudentRepository;
import com.saae.repository.SubjectRepository;
import com.saae.repository.UserRepository;

@Service
public class AdminService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    // User Management
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User createUser(User user) {
        return userService.registerUser(user);
    }

    // Student Management
    public Student saveStudent(Student student) {
        return studentRepository.save(student);
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public void deleteStudent(Long id) {
        studentRepository.deleteById(id);
    }

    // Subject Management
    public Subject saveSubject(Subject subject) {
        return subjectRepository.save(subject);
    }

    public List<Subject> getAllSubjects() {
        return subjectRepository.findAll();
    }

    // Faculty/User Management
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public List<User> getFaculty() {
        return userRepository.findAll().stream()
                .filter(u -> "FACULTY".equals(u.getRole()))
                .toList();
    }

    public Student updateStudent(Long id, Student details) {
        Student s = studentRepository.findById(id).orElseThrow(() -> new RuntimeException("Student not found"));
        s.setRollNumber(details.getRollNumber());
        s.setDepartment(details.getDepartment());
        s.setCurrentSemester(details.getCurrentSemester());
        return studentRepository.save(s);
    }

    public User updateUser(Long id, User details) {
        User u = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        u.setFullName(details.getFullName());
        u.setEmail(details.getEmail());
        return userRepository.save(u);
    }
}
