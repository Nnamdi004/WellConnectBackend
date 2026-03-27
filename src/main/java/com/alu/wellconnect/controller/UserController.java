package com.alu.wellconnect.controller;

import com.alu.wellconnect.dto.UserProfile;
import com.alu.wellconnect.dto.UserUpdateRequest;
import com.alu.wellconnect.entity.User;
import com.alu.wellconnect.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Profile Management", description = "Endpoints for standard users to manage their profiles")
public class UserController {

    private final UserRepository userRepository;

    @GetMapping("/profile")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Get my profile", description = "Fetches the profile of the currently logged-in user")
    public ResponseEntity<UserProfile> getCurrentUser(Authentication authentication) {
        String userEmail = authentication.getName();

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserProfile response = new UserProfile();
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setBio(user.getBio());

        return ResponseEntity.ok(response);
    }

    @PutMapping("/profile")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Update my profile", description = "Updates the username or bio of the currently logged-in user")
    public ResponseEntity<UserProfile> updateProfile(
            Authentication authentication,
            @RequestBody UserUpdateRequest request) {

        String userEmail = authentication.getName();

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (request.getUsername() != null && !request.getUsername().trim().isEmpty()) {
            user.setUsername(request.getUsername());
        }
        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }

        User updatedUser = userRepository.save(user);

        UserProfile response = new UserProfile();
        response.setUsername(updatedUser.getUsername());
        response.setEmail(updatedUser.getEmail());
        response.setBio(updatedUser.getBio());

        return ResponseEntity.ok(response);
    }
}