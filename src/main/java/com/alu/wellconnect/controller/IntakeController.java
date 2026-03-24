package com.alu.wellconnect.controller;

import com.alu.wellconnect.dto.IntakeRequest;
import com.alu.wellconnect.dto.IntakeResponse;
import com.alu.wellconnect.service.IntakeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/intake")
@RequiredArgsConstructor
@Tag(name = "Intake Questionnaire", description = "Clinical intake assessment with PHQ-9 and GAD-7 scoring")
public class IntakeController {

    private final IntakeService intakeService;

    @PostMapping
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Submit intake questionnaire", 
               description = "Submit PHQ-9 and GAD-7 scores. System calculates severity level automatically.")
    public ResponseEntity<IntakeResponse> submitIntake(
            @Valid @RequestBody IntakeRequest request,
            Authentication authentication) {
        String email = authentication.getName();
        IntakeResponse response = intakeService.submitIntake(email, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Get my intake results", 
               description = "Retrieve the logged-in user's intake questionnaire results")
    public ResponseEntity<IntakeResponse> getMyIntake(Authentication authentication) {
        String email = authentication.getName();
        IntakeResponse response = intakeService.getMyIntake(email);
        return ResponseEntity.ok(response);
    }
}
