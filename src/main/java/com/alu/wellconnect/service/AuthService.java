package com.alu.wellconnect.service;

import com.alu.wellconnect.dto.AuthResponse;
import com.alu.wellconnect.dto.LoginRequest;
import com.alu.wellconnect.dto.RegisterRequest;
import com.alu.wellconnect.entity.User;
import com.alu.wellconnect.repository.UserRepository;
import com.alu.wellconnect.util.EncryptionUtil;
import com.alu.wellconnect.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EncryptionUtil encryptionUtil;

    public AuthResponse register(RegisterRequest request) {
        if (request.getEmail() != null && userRepository.existsByEmail(encryptionUtil.encrypt(request.getEmail()))) {
            throw new RuntimeException("Email already exists");
        }

        String username = request.getUsername() != null ? request.getUsername() : generateUsername();
        String encryptedEmail = request.getEmail() != null ? encryptionUtil.encrypt(request.getEmail()) : null;

        User user = User.builder()
                .username(username)
                .email(encryptedEmail)
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(User.Role.USER)
                .status(User.Status.ACTIVE)
                .build();

        userRepository.save(user);

        String token = jwtUtil.generateToken(encryptedEmail != null ? encryptedEmail : username, user.getRole().name());

        return AuthResponse.builder()
                .token(token)
                .username(username)
                .role(user.getRole().name())
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        String encryptedEmail = encryptionUtil.encrypt(request.getEmail());
        User user = userRepository.findByEmail(encryptedEmail)
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }

        if (user.getStatus() == User.Status.SUSPENDED) {
            throw new RuntimeException("Account suspended");
        }

        String token = jwtUtil.generateToken(encryptedEmail, user.getRole().name());

        return AuthResponse.builder()
                .token(token)
                .username(user.getUsername())
                .role(user.getRole().name())
                .build();
    }

    private String generateUsername() {
        String[] prefixes = {"HealingVoice", "BraveHeart", "SilentStrength", "HopefulSoul", "ResilientSpirit"};
        String prefix = prefixes[new Random().nextInt(prefixes.length)];
        String suffix = String.format("%04d", new Random().nextInt(10000));
        return prefix + "_" + suffix;
    }
}
