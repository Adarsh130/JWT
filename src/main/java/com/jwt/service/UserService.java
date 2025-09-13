package com.jwt.service;

import com.jwt.model.User;
import com.jwt.model.Role;
import com.jwt.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User register(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        // Ensure user has at least USER role if no roles are set
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            user.setRoles(new HashSet<>(Collections.singletonList(Role.USER)));
        }
        
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
    
    public User registerAdmin(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        // Set admin role
        Set<Role> adminRoles = new HashSet<>();
        adminRoles.add(Role.ADMIN);
        adminRoles.add(Role.USER); // Admin can also access user endpoints
        user.setRoles(adminRoles);
        
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
