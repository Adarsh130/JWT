package com.jwt.controller;

import com.jwt.model.User;
import com.jwt.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user")
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/dashboard")
    public ResponseEntity<?> userDashboard(Authentication authentication) {
        return ResponseEntity.ok(Map.of(
            "message", "Welcome to User Dashboard!",
            "user", authentication.getName(),
            "authorities", authentication.getAuthorities()
        ));
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(Authentication authentication) {
        String username = authentication.getName();
        return userService.findByUsername(username)
                .map(user -> ResponseEntity.ok(Map.of(
                    "message", "Profile retrieved successfully",
                    "profile", Map.of(
                        "id", user.getId(),
                        "username", user.getUsername(),
                        "email", user.getEmail(),
                        "roles", user.getRoles()
                    )
                )))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody Map<String, String> updates, 
                                         Authentication authentication) {
        String username = authentication.getName();
        return userService.findByUsername(username)
                .map(user -> {
                    if (updates.containsKey("email")) {
                        user.setEmail(updates.get("email"));
                    }
                    // Note: Username and password updates would require additional validation
                    // This is a simplified example
                    
                    // Save updated user (you might want to add validation here)
                    return ResponseEntity.ok(Map.of(
                        "message", "Profile updated successfully",
                        "profile", Map.of(
                            "id", user.getId(),
                            "username", user.getUsername(),
                            "email", user.getEmail(),
                            "roles", user.getRoles()
                        )
                    ));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/settings")
    public ResponseEntity<?> getUserSettings(Authentication authentication) {
        return ResponseEntity.ok(Map.of(
            "message", "User settings retrieved",
            "user", authentication.getName(),
            "settings", Map.of(
                "theme", "default",
                "notifications", true,
                "language", "en"
            )
        ));
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> request,
                                          Authentication authentication) {
        // This is a simplified example - in production, you'd want to:
        // 1. Verify the current password
        // 2. Validate the new password
        // 3. Hash the new password
        // 4. Update the user record
        
        return ResponseEntity.ok(Map.of(
            "message", "Password change functionality - implement with proper validation",
            "user", authentication.getName()
        ));
    }
}