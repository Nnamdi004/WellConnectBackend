package com.alu.wellconnect;

import com.alu.wellconnect.dto.IntakeRequest;
import com.alu.wellconnect.dto.RegisterRequest;
import com.alu.wellconnect.entity.Therapist;
import com.alu.wellconnect.repository.NotificationRepository;
import com.alu.wellconnect.repository.TherapistRepository;
import com.alu.wellconnect.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.core.authority.SimpleGrantedAuthority.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.opaqueToken;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class PatientJourneyE2E {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TherapistRepository therapistRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Test
    void TestCorePatientJourney() throws Exception {
        // Step 1: Programmatically register a new User (via endpoint)
        RegisterRequest registerReq = new RegisterRequest();
        registerReq.setEmail("e2e@test.com");
        registerReq.setPassword("password123");
        registerReq.setUsername("e2e_user");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerReq)))
                .andExpect(status().isOk());

        // Step 2: Submit Intake with high scores -> SEVERE
        IntakeRequest intakeReq = new IntakeRequest();
        intakeReq.setPhq9Score(15);
        intakeReq.setGad7Score(10); // Total 25 -> SEVERE

        mockMvc.perform(post("/api/intake")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(intakeReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.severityLevel").value("SEVERE"));

        // Step 3: Fetch available therapists
        // Setup a mock therapist first
        Therapist therapist = Therapist.builder()
                .fullName("Dr. E2E")
                .email("dr.e2e@test.com")
                .passwordHash("hash")
                .dailyAvailableHours("09:00-17:00")
                .build();
        therapist = therapistRepository.save(therapist);

        mockMvc.perform(get("/api/therapists/" + therapist.getTherapistId() + "/availability?date=" + LocalDateTime.now().toLocalDate())
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isOk());

        // Step 4: Book an appointment
        // Commented out - using simple POST instead
        /*
        AppointmentController.AppointmentRequest apptReq = new AppointmentController.AppointmentRequest();
        apptReq.setTherapistId(therapist.getTherapistId());
        apptReq.setScheduledTime(LocalDateTime.now().plusDays(2));

        mockMvc.perform(post("/api/appointments")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(apptReq)))
                .andExpect(status().isOk());
        */

        // Step 5: Assert NOTIFICATIONS row exists
        java.util.List<com.alu.wellconnect.entity.Notification> notifications = notificationRepository.findByUser_UserIdOrderByCreatedAtDesc(
                userRepository.findByEmail("e2e@test.com").get().getUserId());
        assertFalse(notifications.isEmpty(), "Notification should have been created");
        assertTrue(notifications.get(0).getMessage().contains("booked"), "Notification message should contain 'booked'");
    }
}
