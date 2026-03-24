package com.alu.wellconnect.controller;

import com.alu.wellconnect.dto.CreateReportRequest;
import com.alu.wellconnect.dto.UpdateReportRequest;
import com.alu.wellconnect.entity.ContentReport;
import com.alu.wellconnect.repository.UserRepository;
import com.alu.wellconnect.service.ContentReportService;
import com.alu.wellconnect.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ContentReportController {

    private final ContentReportService contentReportService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @PostMapping("/reports")
    public ResponseEntity<ContentReport> createReport(
            @Valid @RequestBody CreateReportRequest request,
            @RequestHeader("Authorization") String token) {

        String email = jwtUtil.extractEmail(token.substring(7));
        Long userId = extractUserIdFromEmail(email);

        ContentReport report = contentReportService.createReport(request, userId);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/admin/reports")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Get all pending reports (Admin only)")
    public ResponseEntity<List<ContentReport>> getPendingReports() {
        return ResponseEntity.ok(contentReportService.getPendingReports());
    }

    @PutMapping("/admin/reports/{reportId}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Update report status (Admin only)")
    public ResponseEntity<ContentReport> updateReport(
            @PathVariable Long reportId,
            @Valid @RequestBody UpdateReportRequest request,
            @RequestHeader("Authorization") String token) {

        String email = jwtUtil.extractEmail(token.substring(7));
        Long adminId = extractUserIdFromEmail(email);

        ContentReport report = contentReportService.updateReportStatus(reportId, request, adminId);
        return ResponseEntity.ok(report);
    }

    @PutMapping("/admin/reports/{reportId}/resolve")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Resolve a report: removes the story and optionally suspends the user (Admin only)")
    public ResponseEntity<ContentReport> resolveReport(
            @PathVariable Long reportId,
            @RequestHeader("Authorization") String token) {

        String email = jwtUtil.extractEmail(token.substring(7));
        Long adminId = extractUserIdFromEmail(email);

        ContentReport resolved = contentReportService.resolveReport(reportId, adminId);
        return ResponseEntity.ok(resolved);
    }

    private Long extractUserIdFromEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found for email: " + email))
                .getUserId();
    }
}
