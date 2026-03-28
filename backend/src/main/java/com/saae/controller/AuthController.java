package com.saae.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.saae.model.User;
import com.saae.service.UserService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginRequest) {
        if (loginRequest.getUsername() == null || loginRequest.getPassword() == null) {
            return ResponseEntity.badRequest().body("Username and password are required");
        }
        String username = loginRequest.getUsername().trim();
        String password = loginRequest.getPassword();

        System.out.println("Login attempt for username: [" + username + "]");
        try {
            Optional<User> user = userService.loginUser(username, password);
            if (user.isPresent()) {
                User foundUser = user.get();
                System.out.println(
                        "Login successful for user: " + foundUser.getUsername() + " with role: " + foundUser.getRole());
                return ResponseEntity.ok(foundUser);
            } else {
                System.out.println("Login failed: Invalid credentials for user: " + loginRequest.getUsername());
                return ResponseEntity.status(401).body("Invalid username or password");
            }
        } catch (Exception e) {
            System.err.println("CRITICAL Login error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Internal Server Error: " + e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        // Simple registration for demo purposes
        // In real app, password should be hashed
        return ResponseEntity.ok(userService.registerUser(user));
    }
}
