package com.alu.wellconnect.controller;

import com.alu.wellconnect.dto.AuthResponse;
import com.alu.wellconnect.dto.LoginRequest;
import com.alu.wellconnect.dto.RegisterRequest;
import com.alu.wellconnect.service.AuthService;
import com.alu.wellconnect.security.RateLimit;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @RateLimit(capacity = 3, seconds = 3600) // 3 registrations per hour per IP
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    @RateLimit(capacity = 5, seconds = 60) // 5 logins per minute per IP
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
