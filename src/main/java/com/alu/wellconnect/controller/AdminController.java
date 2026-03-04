package com.alu.wellconnect.controller;

import com.alu.wellconnect.dto.AdminRegisterRequest;
import com.alu.wellconnect.dto.AuthResponse;
import com.alu.wellconnect.dto.LoginRequest;
import com.alu.wellconnect.entity.User;
import com.alu.wellconnect.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Admin Management", description = "Admin authentication and management endpoints")
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/auth/admin/login")
    @Operation(summary = "Admin login")
    public ResponseEntity<AuthResponse> loginAdmin(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(adminService.loginAdmin(request));
    }

    @PostMapping("/admin/admins")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Create new admin (Admin only)")
    public ResponseEntity<User> createAdmin(@Valid @RequestBody AdminRegisterRequest request) {
        return ResponseEntity.ok(adminService.createAdmin(request));
    }

    @GetMapping("/admin/admins")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Get all admins (Admin only)")
    public ResponseEntity<List<User>> getAllAdmins() {
        return ResponseEntity.ok(adminService.getAllAdmins());
    }

    @PutMapping("/admin/admins/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Update admin (Admin only)")
    public ResponseEntity<User> updateAdmin(@PathVariable Long id, @Valid @RequestBody AdminRegisterRequest request) {
        return ResponseEntity.ok(adminService.updateAdmin(id, request));
    }

    @DeleteMapping("/admin/admins/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Delete admin (Admin only)")
    public ResponseEntity<Void> deleteAdmin(@PathVariable Long id) {
        adminService.deleteAdmin(id);
        return ResponseEntity.noContent().build();
    }
}
