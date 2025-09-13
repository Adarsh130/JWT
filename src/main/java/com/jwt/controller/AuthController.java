package com.jwt.controller;

import com.jwt.config.JwtService;
import com.jwt.model.User;
import com.jwt.model.Role;
import com.jwt.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Collections;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody User user) {
        try {
            User savedUser = userService.register(user);
            return ResponseEntity.ok(Map.of(
                "message", "User registered successfully", 
                "userId", savedUser.getId(),
                "roles", savedUser.getRoles().stream().map(Role::getValue).collect(Collectors.toList())
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/register-admin")
    public ResponseEntity<?> registerAdmin(@Valid @RequestBody User user) {
        try {
            User savedUser = userService.registerAdmin(user);
            return ResponseEntity.ok(Map.of(
                "message", "Admin registered successfully", 
                "userId", savedUser.getId(),
                "roles", savedUser.getRoles().stream().map(Role::getValue).collect(Collectors.toList())
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            // Get user details to include roles in token
            User user = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            // Create claims with roles
            Map<String, Object> claims = new HashMap<>();
            List<String> roles = user.getRoles().stream()
                    .map(Role::getValue)
                    .collect(Collectors.toList());
            claims.put("roles", roles);
            
            String token = jwtService.generateToken(username, claims);
            
            return ResponseEntity.ok(Map.of(
                "token", token,
                "username", username,
                "roles", roles
            ));
        } catch (AuthenticationException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid username or password"));
        }
    }

    @GetMapping("/test")
    public String test() {
        return "API is working!";
    }

    @GetMapping("/protected")
    public ResponseEntity<?> protectedEndpoint() {
        return ResponseEntity.ok(Map.of("message", "This is a protected endpoint!"));
    }
    
    @PostMapping("/validate-token")
    public ResponseEntity<?> validateToken(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        
        if (token == null || token.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Token is required"));
        }
        
        try {
            String username = jwtService.extractUsername(token);
            boolean isExpired = jwtService.isTokenExpiredPublic(token);
            List<String> roles = jwtService.extractRoles(token);
            
            return ResponseEntity.ok(Map.of(
                "valid", !isExpired,
                "expired", isExpired,
                "username", username,
                "roles", roles != null ? roles : List.of()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Invalid token",
                "details", e.getMessage()
            ));
        }
    }
}
