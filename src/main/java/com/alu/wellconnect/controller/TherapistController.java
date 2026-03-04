package com.alu.wellconnect.controller;

import com.alu.wellconnect.dto.*;
import com.alu.wellconnect.service.TherapistService;
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
@Tag(name = "Therapist Management", description = "Therapist authentication and management endpoints")
public class TherapistController {

    private final TherapistService therapistService;

    @PostMapping("/auth/therapist/login")
    @Operation(summary = "Therapist login with status validation")
    public ResponseEntity<AuthResponse> loginTherapist(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(therapistService.loginTherapist(request));
    }

    @PostMapping("/admin/therapists/register")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Admin registers a new therapist")
    public ResponseEntity<TherapistResponse> registerTherapist(@Valid @RequestBody TherapistRegisterRequest request) {
        return ResponseEntity.ok(therapistService.registerTherapist(request));
    }

    @GetMapping("/admin/therapists")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Get all therapists (Admin only)")
    public ResponseEntity<List<TherapistResponse>> getAllTherapists() {
        return ResponseEntity.ok(therapistService.getAllTherapists());
    }

    @GetMapping("/admin/therapists/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Get therapist by ID (Admin only)")
    public ResponseEntity<TherapistResponse> getTherapistById(@PathVariable Long id) {
        return ResponseEntity.ok(therapistService.getTherapistById(id));
    }

    @PutMapping("/admin/therapists/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Update therapist (Admin only)")
    public ResponseEntity<TherapistResponse> updateTherapist(@PathVariable Long id, @Valid @RequestBody TherapistUpdateRequest request) {
        return ResponseEntity.ok(therapistService.updateTherapist(id, request));
    }

    @DeleteMapping("/admin/therapists/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Delete therapist (Admin only)")
    public ResponseEntity<Void> deleteTherapist(@PathVariable Long id) {
        therapistService.deleteTherapist(id);
        return ResponseEntity.noContent().build();
    }
}
