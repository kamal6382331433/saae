package com.saae.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.saae.model.User;
import com.saae.service.UserService;

@RestController
@RequestMapping("/api/setup")
public class SetupController {

    @Autowired
    private UserService userService;

    @PostMapping("/create-admin")
    public ResponseEntity<String> createAdmin() {
        try {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword("admin123");
            admin.setRole("ADMIN");
            admin.setFullName("System Administrator");
            admin.setEmail("admin@saae.com");

            userService.registerUser(admin);

            return ResponseEntity.ok("Admin user created successfully! You can now login with: admin / admin123");
        } catch (Exception e) {
            return ResponseEntity.ok("Admin user already exists or error: " + e.getMessage());
        }
    }
}
