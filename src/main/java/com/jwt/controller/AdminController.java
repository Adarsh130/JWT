package com.jwt.controller;

import com.jwt.model.User;
import com.jwt.service.UserService;
import com.jwt.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/dashboard")
    public ResponseEntity<?> adminDashboard(Authentication authentication) {
        return ResponseEntity.ok(Map.of(
            "message", "Welcome to Admin Dashboard!",
            "user", authentication.getName(),
            "authorities", authentication.getAuthorities()
        ));
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(Map.of(
            "message", "All users retrieved successfully",
            "users", users,
            "count", users.size()
        ));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserById(@PathVariable String id) {
        return userRepository.findById(id)
                .map(user -> ResponseEntity.ok(Map.of("user", user)))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable String id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getSystemStats() {
        long totalUsers = userRepository.count();
        long adminUsers = userRepository.findAll().stream()
                .mapToLong(user -> user.isAdmin() ? 1 : 0)
                .sum();
        long regularUsers = totalUsers - adminUsers;

        return ResponseEntity.ok(Map.of(
            "totalUsers", totalUsers,
            "adminUsers", adminUsers,
            "regularUsers", regularUsers
        ));
    }

    @PostMapping("/promote/{id}")
    public ResponseEntity<?> promoteToAdmin(@PathVariable String id) {
        return userRepository.findById(id)
                .map(user -> {
                    user.addRole(com.jwt.model.Role.ADMIN);
                    userRepository.save(user);
                    return ResponseEntity.ok(Map.of(
                        "message", "User promoted to admin successfully",
                        "user", user
                    ));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}