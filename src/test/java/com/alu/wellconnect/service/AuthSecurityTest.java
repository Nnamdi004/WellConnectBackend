package com.alu.wellconnect.service;

import com.alu.wellconnect.dto.AuthResponse;
import com.alu.wellconnect.dto.LoginRequest;
import com.alu.wellconnect.dto.RegisterRequest;
import com.alu.wellconnect.entity.User;
import com.alu.wellconnect.repository.UserRepository;
import com.alu.wellconnect.util.EncryptionUtil;
import com.alu.wellconnect.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthSecurityTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private EncryptionUtil encryptionUtil;

    @InjectMocks
    private AuthService authService;

    @Test
    void TestPasswordHashing_OnRegister() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");
        request.setPassword("plainPassword");
        request.setUsername("testuser");

        when(encryptionUtil.encrypt(anyString())).thenReturn("encryptedEmail");
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode("plainPassword")).thenReturn("hashedPassword");
        when(jwtUtil.generateToken(anyString(), anyString())).thenReturn("mockToken");

        authService.register(request);

        // Verify that the password saved is NOT the plain text
        verify(passwordEncoder).encode("plainPassword");
        verify(userRepository).save(argThat(user -> 
            user.getPasswordHash().equals("hashedPassword") && 
            !user.getPasswordHash().equals("plainPassword")
        ));
    }

    @Test
    void TestJwtGeneration_OnLogin() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("password");

        User user = User.builder()
                .email("encryptedEmail")
                .passwordHash("hashedPassword")
                .role(User.Role.USER)
                .status(User.Status.ACTIVE)
                .build();

        when(encryptionUtil.encrypt("test@example.com")).thenReturn("encryptedEmail");
        when(userRepository.findByEmail("encryptedEmail")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "hashedPassword")).thenReturn(true);
        when(jwtUtil.generateToken("encryptedEmail", "USER")).thenReturn("valid.jwt.token");

        AuthResponse response = authService.login(request);

        assertNotNull(response.getToken());
        assertEquals("valid.jwt.token", response.getToken());
    }

    @Test
    void TestInvalidLogin_ThrowsException() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("wrongPassword");

        User user = User.builder()
                .email("encryptedEmail")
                .passwordHash("hashedPassword")
                .status(User.Status.ACTIVE)
                .build();

        when(encryptionUtil.encrypt("test@example.com")).thenReturn("encryptedEmail");
        when(userRepository.findByEmail("encryptedEmail")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPassword", "hashedPassword")).thenReturn(false);

        Exception exception = assertThrows(RuntimeException.class, () -> authService.login(request));
        assertEquals("Invalid credentials", exception.getMessage());
    }
}
