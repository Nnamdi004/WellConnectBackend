package com.alu.wellconnect.config;

import com.alu.wellconnect.entity.User;
import com.alu.wellconnect.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedAdmin();
    }

    private void seedAdmin() {
        String adminEmail = "admin@wellconnect.com";
        
        if (!userRepository.existsByEmail(adminEmail)) {
            User admin = User.builder()
                    .username("admin")
                    .email(adminEmail)
                    .passwordHash(passwordEncoder.encode("admin123"))
                    .role(User.Role.ADMIN)
                    .status(User.Status.ACTIVE)
                    .build();
            
            userRepository.save(admin);
            log.info("Default admin created - Email: {}, Password: admin123", adminEmail);
        } else {
            log.info("Admin already exists");
        }
    }
}
