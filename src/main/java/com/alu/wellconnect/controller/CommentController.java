package com.alu.wellconnect.controller;

import com.alu.wellconnect.dto.CommentRequest;
import com.alu.wellconnect.dto.CommentResponse;
import com.alu.wellconnect.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/stories/{storyId}/comments")
@RequiredArgsConstructor
@Tag(name = "Comments", description = "Endpoints for interacting with story comments")
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Add a comment to a story")
    public ResponseEntity<CommentResponse> addComment(
            @PathVariable Long storyId,
            @RequestBody CommentRequest request,
            Principal principal) {
        return ResponseEntity.ok(commentService.addComment(storyId, request, principal.getName()));
    }

    @GetMapping
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Get all comments for a story")
    public ResponseEntity<List<CommentResponse>> getComments(@PathVariable Long storyId) {
        return ResponseEntity.ok(commentService.getComments(storyId));
    }
}
