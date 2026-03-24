package com.alu.wellconnect.controller;

import com.alu.wellconnect.dto.SessionCloseRequest;
import com.alu.wellconnect.dto.SessionResponse;
import com.alu.wellconnect.dto.SessionStartRequest;
import com.alu.wellconnect.service.ChatSessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Tag(name = "Chat Management", description = "Endpoints for managing chat sessions")
public class ChatController {

    private final ChatSessionService chatSessionService;

    @PostMapping("/{appointmentId}/start")
    @Operation(summary = "Start a chat session", description = "Verifies confirmed appointment and logs opening mood")
    public ResponseEntity<Long> startSession(@PathVariable Long appointmentId, @Valid @RequestBody SessionStartRequest request) {
        Long sessionId = chatSessionService.startSession(appointmentId, request);
        return ResponseEntity.ok(sessionId);
    }

    @PutMapping("/{sessionId}/close")
    @Operation(summary = "Close a chat session", description = "Updates status to CLOSED and logs closing mood")
    public ResponseEntity<SessionResponse> closeSession(@PathVariable Long sessionId, @Valid @RequestBody SessionCloseRequest request) {
        SessionResponse response = chatSessionService.closeSession(sessionId, request);
        return ResponseEntity.ok(response);
    }
}
