package com.alu.wellconnect.service;

import com.alu.wellconnect.dto.AdminRegisterRequest;
import com.alu.wellconnect.dto.AuthResponse;
import com.alu.wellconnect.dto.LoginRequest;
import com.alu.wellconnect.entity.User;
import com.alu.wellconnect.repository.UserRepository;
import com.alu.wellconnect.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public User createAdmin(AdminRegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User admin = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(User.Role.ADMIN)
                .status(User.Status.ACTIVE)
                .build();

        return userRepository.save(admin);
    }

    public AuthResponse loginAdmin(LoginRequest request) {
        User admin = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (admin.getRole() != User.Role.ADMIN) {
            throw new RuntimeException("Access denied");
        }

        if (!passwordEncoder.matches(request.getPassword(), admin.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtUtil.generateToken(admin.getEmail(), admin.getRole().name());

        return AuthResponse.builder()
                .token(token)
                .email(admin.getEmail())
                .role(admin.getRole().name())
                .build();
    }

    public List<User> getAllAdmins() {
        return userRepository.findAll().stream()
                .filter(user -> user.getRole() == User.Role.ADMIN)
                .toList();
    }

    public User updateAdmin(Long id, AdminRegisterRequest request) {
        User admin = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        if (admin.getRole() != User.Role.ADMIN) {
            throw new RuntimeException("User is not an admin");
        }

        if (request.getUsername() != null) {
            admin.setUsername(request.getUsername());
        }
        if (request.getEmail() != null) {
            admin.setEmail(request.getEmail());
        }
        if (request.getPassword() != null) {
            admin.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }

        return userRepository.save(admin);
    }

    public void deleteAdmin(Long id) {
        User admin = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        if (admin.getRole() != User.Role.ADMIN) {
            throw new RuntimeException("User is not an admin");
        }

        userRepository.delete(admin);
    }
}
