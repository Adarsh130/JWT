package com.jwt.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/public")
    public ResponseEntity<?> publicEndpoint() {
        return ResponseEntity.ok(Map.of(
            "message", "This is a public endpoint - no authentication required"
        ));
    }

    @GetMapping("/authenticated")
    public ResponseEntity<?> authenticatedEndpoint(Authentication authentication) {
        return ResponseEntity.ok(Map.of(
            "message", "This endpoint requires authentication",
            "user", authentication.getName(),
            "authorities", authentication.getAuthorities()
        ));
    }

    @GetMapping("/user-only")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> userOnlyEndpoint(Authentication authentication) {
        return ResponseEntity.ok(Map.of(
            "message", "This endpoint is for USER role only",
            "user", authentication.getName(),
            "authorities", authentication.getAuthorities()
        ));
    }

    @GetMapping("/admin-only")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> adminOnlyEndpoint(Authentication authentication) {
        return ResponseEntity.ok(Map.of(
            "message", "This endpoint is for ADMIN role only",
            "user", authentication.getName(),
            "authorities", authentication.getAuthorities()
        ));
    }

    @GetMapping("/user-or-admin")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> userOrAdminEndpoint(Authentication authentication) {
        return ResponseEntity.ok(Map.of(
            "message", "This endpoint is for USER or ADMIN roles",
            "user", authentication.getName(),
            "authorities", authentication.getAuthorities()
        ));
    }
}