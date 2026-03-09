package com.alu.wellconnect.controller;

import com.alu.wellconnect.dto.CreateMoodRequest;
import com.alu.wellconnect.entity.MoodLog;
import com.alu.wellconnect.service.MoodLogService;
import com.alu.wellconnect.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/moods")
@RequiredArgsConstructor
public class MoodLogController {

    private final MoodLogService moodLogService;
    private final JwtUtil jwtUtil;

    @PostMapping
    public ResponseEntity<MoodLog> createMoodLog(
            @Valid @RequestBody CreateMoodRequest request,
            @RequestHeader("Authorization") String token) {

        String email = jwtUtil.extractEmail(token.substring(7));
        Long userId = extractUserIdFromEmail(email);

        MoodLog moodLog = moodLogService.createMoodLog(request, userId);
        return ResponseEntity.ok(moodLog);
    }

    @GetMapping
    public ResponseEntity<List<MoodLog>> getUserMoodHistory(
            @RequestHeader("Authorization") String token) {

        String email = jwtUtil.extractEmail(token.substring(7));
        Long userId = extractUserIdFromEmail(email);

        List<MoodLog> moodHistory = moodLogService.getUserMoodHistory(userId);
        return ResponseEntity.ok(moodHistory);
    }

    private Long extractUserIdFromEmail(String email) {
        // TODO: Implement user lookup by email
        return 1L;
    }
}
