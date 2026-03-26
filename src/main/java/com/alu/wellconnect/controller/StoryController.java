package com.alu.wellconnect.controller;

import com.alu.wellconnect.dto.StoryRequest;
import com.alu.wellconnect.dto.StoryResponse;
import com.alu.wellconnect.service.StoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stories")
@RequiredArgsConstructor
public class StoryController {

    private final StoryService storyService;

    @GetMapping
    @Operation(summary = "Get public story feed")
    public ResponseEntity<List<StoryResponse>> getPublicFeed() {
        return ResponseEntity.ok(storyService.getPublicFeed());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get story by ID")
    public ResponseEntity<StoryResponse> getStoryById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(storyService.getStoryById(id));
        } catch (Exception e) {
            return ResponseEntity.status(404).build();
        }
    }

    @PostMapping
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Create a new story")
    public ResponseEntity<StoryResponse> createStory(@RequestBody StoryRequest request, java.security.Principal principal) {
        return ResponseEntity.ok(storyService.createStory(request, principal.getName()));
    }

    @PostMapping("/{id}/like")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Like a story")
    public ResponseEntity<Void> likeStory(@PathVariable Long id, java.security.Principal principal) {
        storyService.likeStory(id, principal.getName());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/like")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Unlike a story")
    public ResponseEntity<Void> unlikeStory(@PathVariable Long id, java.security.Principal principal) {
        storyService.unlikeStory(id, principal.getName());
        return ResponseEntity.noContent().build();
    }
}
