package com.alu.wellconnect.controller;

import com.alu.wellconnect.dto.CreateReportRequest;
import com.alu.wellconnect.dto.UpdateReportRequest;
import com.alu.wellconnect.entity.ContentReport;
import com.alu.wellconnect.service.ContentReportService;
import com.alu.wellconnect.util.JwtUtil;
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
    public ResponseEntity<List<ContentReport>> getPendingReports() {
        return ResponseEntity.ok(contentReportService.getPendingReports());
    }

    @PutMapping("/admin/reports/{reportId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ContentReport> updateReport(
            @PathVariable Long reportId,
            @Valid @RequestBody UpdateReportRequest request,
            @RequestHeader("Authorization") String token) {

        String email = jwtUtil.extractEmail(token.substring(7));
        Long adminId = extractUserIdFromEmail(email);

        ContentReport report = contentReportService.updateReportStatus(reportId, request, adminId);
        return ResponseEntity.ok(report);
    }

    private Long extractUserIdFromEmail(String email) {
        // TODO: Implement user lookup by email
        return 1L;
    }
}
