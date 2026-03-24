package com.alu.wellconnect.config;

import com.alu.wellconnect.entity.User;
import com.alu.wellconnect.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        seedAdmin();
    }

    private void seedAdmin() {
        if (!userRepository.existsByEmail(adminEmail)) {
            User admin = User.builder()
                    .username("admin")
                    .email(adminEmail)
                    .passwordHash(passwordEncoder.encode(adminPassword))
                    .role(User.Role.ADMIN)
                    .status(User.Status.ACTIVE)
                    .build();
            
            userRepository.save(admin);
            log.info("Default admin created - Email: {}, Password: [HIDDEN]", adminEmail);
        } else {
            log.info("Admin already exists");
        }
    }
}
