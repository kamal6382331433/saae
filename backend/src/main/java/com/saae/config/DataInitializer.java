package com.saae.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.saae.model.User;
import com.saae.repository.UserRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        long count = userRepository.count();
        System.out.println("DEBUG: Current user count in DB: " + count);

        if (count == 0) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole("ADMIN");
            admin.setFullName("System Administrator");
            userRepository.save(admin);

            User faculty = new User();
            faculty.setUsername("faculty");
            faculty.setPassword(passwordEncoder.encode("faculty123"));
            faculty.setRole("FACULTY");
            faculty.setFullName("Faculty User");
            userRepository.save(faculty);

            User student = new User();
            student.setUsername("student");
            student.setPassword(passwordEncoder.encode("student123"));
            student.setRole("STUDENT");
            student.setFullName("Student User");
            userRepository.save(student);

            System.out.println(
                    "Default users created with hashed passwords: admin/admin123, faculty/faculty123, student/student123");
        } else {
            System.out.println("DEBUG: Users already exist. Skipping default creation.");
        }
    }
}
