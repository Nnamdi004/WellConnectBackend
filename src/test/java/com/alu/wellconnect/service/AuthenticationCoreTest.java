package com.alu.wellconnect.service;

import com.alu.wellconnect.dto.AuthResponse;
import com.alu.wellconnect.dto.LoginRequest;
import com.alu.wellconnect.dto.RegisterRequest;
import com.alu.wellconnect.entity.User;
import com.alu.wellconnect.repository.UserRepository;
import com.alu.wellconnect.util.EncryptionUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class AuthenticationCoreTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EncryptionUtil encryptionUtil;

    // --- TEST 1: Password Hashing ---
    @Test
    public void testRegister_HashesPasswordSuccessfully() {
        String plainPassword = "MySecretPassword123!";
        String email = "secure@alu.edu";
        RegisterRequest request = new RegisterRequest();
        request.setEmail(email);
        request.setPassword(plainPassword);
        request.setUsername("secure_user");

        // Act
        authService.register(request);

        // Fetch saved user
        User savedUser = userRepository.findByEmail(encryptionUtil.encrypt(email))
                .orElseThrow(() -> new RuntimeException("User not saved"));

        // Assert: The DB password must NOT be the plain text string
        assertNotEquals(plainPassword, savedUser.getPasswordHash());
        // Assert: Password encoder can still mathematically verify it
        assertTrue(passwordEncoder.matches(plainPassword, savedUser.getPasswordHash()));
    }

    // --- TEST 2: JWT Generation ---
    @Test
    public void testLogin_ValidCredentials_ReturnsJWT() {
        // Arrange
        String email = "login_test@alu.edu";
        String password = "TestPass123!";
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail(email);
        registerRequest.setPassword(password);
        registerRequest.setUsername("login_test_user");
        authService.register(registerRequest);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(email);
        loginRequest.setPassword(password);

        // Act
        AuthResponse response = authService.login(loginRequest);

        // Assert: Ensure the JWT is generated and formatted correctly
        assertNotNull(response);
        assertNotNull(response.getToken());
        assertTrue(response.getToken().startsWith("eyJ"), "JWT should start with typical header");
    }

    // --- TEST 3: Invalid Login Exception ---
    @Test
    public void testLogin_InvalidPassword_ThrowsException() {
        // Arrange: Register a user
        String email = "wrong_pass@alu.edu";
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail(email);
        registerRequest.setPassword("CorrectPassword123!");
        authService.register(registerRequest);

        // Act & Assert: Ensure it throws an exception for wrong password
        LoginRequest badLogin = new LoginRequest();
        badLogin.setEmail(email);
        badLogin.setPassword("WrongPassword999!");

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.login(badLogin);
        }, "A wrong password must throw an exception");
        
        assertEquals("Invalid credentials", exception.getMessage());
    }
}
