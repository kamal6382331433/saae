package com.saae.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.saae.model.User;
import com.saae.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public Optional<User> loginUser(String username, String password) {
        System.out.println("DEBUG: Checking login for username: [" + username + "]");
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            System.out.println("DEBUG: User found in DB. Role: " + user.getRole());

            boolean matches = passwordEncoder.matches(password, user.getPassword());
            System.out.println("DEBUG: Password match result: " + matches);

            if (matches) {
                return Optional.of(user);
            } else {
                // Check if it's a plain text match (only for debugging/emergency)
                if (password.equals(user.getPassword())) {
                    System.out.println(
                            "DEBUG: Password matches PLAIN TEXT but the encoder rejected it. PASSWORD NEEDS HASHING.");
                }
            }
        } else {
            System.out.println("DEBUG: No user found with username: [" + username + "]");
        }
        return Optional.empty();
    }
}
