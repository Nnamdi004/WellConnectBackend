package com.alu.wellconnect.service;

import com.alu.wellconnect.dto.*;
import com.alu.wellconnect.entity.Therapist;
import com.alu.wellconnect.repository.TherapistRepository;
import com.alu.wellconnect.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TherapistService {

    private final TherapistRepository therapistRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public TherapistResponse registerTherapist(TherapistRegisterRequest request) {
        if (therapistRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        Therapist therapist = Therapist.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .specialisation(request.getSpecialisation())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .status(Therapist.Status.ACTIVE)
                .build();

        Therapist saved = therapistRepository.save(therapist);
        return mapToResponse(saved);
    }

    public AuthResponse loginTherapist(LoginRequest request) {
        Therapist therapist = therapistRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), therapist.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }

        if (therapist.getStatus() != Therapist.Status.ACTIVE) {
            throw new RuntimeException("Account is " + therapist.getStatus().name());
        }

        String token = jwtUtil.generateToken(therapist.getEmail(), "THERAPIST");

        return AuthResponse.builder()
                .token(token)
                .email(therapist.getEmail())
                .role("THERAPIST")
                .build();
    }

    public List<TherapistResponse> getAllTherapists() {
        return therapistRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public TherapistResponse getTherapistById(Long id) {
        Therapist therapist = therapistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Therapist not found"));
        return mapToResponse(therapist);
    }

    public TherapistResponse updateTherapist(Long id, TherapistUpdateRequest request) {
        Therapist therapist = therapistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Therapist not found"));

        if (request.getFullName() != null) {
            therapist.setFullName(request.getFullName());
        }
        if (request.getEmail() != null) {
            therapist.setEmail(request.getEmail());
        }
        if (request.getSpecialisation() != null) {
            therapist.setSpecialisation(request.getSpecialisation());
        }
        if (request.getStatus() != null) {
            therapist.setStatus(request.getStatus());
        }

        Therapist updated = therapistRepository.save(therapist);
        return mapToResponse(updated);
    }

    public void deleteTherapist(Long id) {
        Therapist therapist = therapistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Therapist not found"));
        therapistRepository.delete(therapist);
    }

    private TherapistResponse mapToResponse(Therapist therapist) {
        return TherapistResponse.builder()
                .therapistId(therapist.getTherapistId())
                .fullName(therapist.getFullName())
                .email(therapist.getEmail())
                .specialisation(therapist.getSpecialisation())
                .status(therapist.getStatus())
                .createdAt(therapist.getCreatedAt())
                .build();
    }
}
