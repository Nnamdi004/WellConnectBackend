package com.alu.wellconnect.controller;

import com.alu.wellconnect.dto.*;
import com.alu.wellconnect.entity.Therapist;
import com.alu.wellconnect.repository.TherapistRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional // Cleans up the database automatically after the test finishes!
public class EndToEndPatientJourneyTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TherapistRepository therapistRepository;

    @Test
    public void testFullPatientJourney() throws Exception {
        
        // ====================================================================
        // STEP 1: Register & Login
        // ====================================================================
        String registerJson = "{ \"email\": \"e2e_patient@alu.edu\", \"password\": \"TestPass123!\", \"username\": \"e2e_patient\" }";
        
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registerJson))
                .andExpect(status().isOk());

        String loginJson = "{ \"email\": \"e2e_patient@alu.edu\", \"password\": \"TestPass123!\" }";
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = loginResult.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        String token = jsonNode.get("token").asText();
        
        assertNotNull(token, "Token should not be null after successful login");

        // ====================================================================
        // STEP 2: Submit an Intake form with high scores (Expect SEVERE)
        // ====================================================================
        String intakeJson = "{ \"phq9Score\": 15, \"gad7Score\": 10 }"; // Total 25 -> SEVERE
        
        mockMvc.perform(post("/api/intake")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(intakeJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.severityLevel").value("SEVERE"));

        // ====================================================================
        // STEP 3: Fetch available therapists
        // ====================================================================
        // Ensure at least one therapist exists
        if (therapistRepository.count() == 0) {
            Therapist t = Therapist.builder()
                    .fullName("Mock Therapist")
                    .email("mock_therapist@wellconnect.com")
                    .passwordHash("hashed")
                    .specialisation("General")
                    .build();
            therapistRepository.save(t);
        }

        MvcResult therapistsResult = mockMvc.perform(get("/api/therapists")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();
                
        String therapistsBody = therapistsResult.getResponse().getContentAsString();
        JsonNode therapistsNode = objectMapper.readTree(therapistsBody);
        
        assertTrue(therapistsNode.isArray() && therapistsNode.size() > 0, "No therapists found in DB");
        long therapistId = therapistsNode.get(0).get("therapistId").asLong(); 

        // ====================================================================
        // STEP 4: Book an appointment
        // ====================================================================
        String appointmentJson = "{ \"therapistId\": " + therapistId + ", \"scheduledTime\": \"2026-03-30T10:00:00\" }";
        
        mockMvc.perform(post("/api/appointments")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(appointmentJson))
                .andExpect(status().isOk()); // It seems the controller returns 200 OK in this codebase

        // ====================================================================
        // STEP 5: Assert that a NOTIFICATIONS row was successfully created
        // ====================================================================
        mockMvc.perform(get("/api/notifications/me")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].type").value("APPOINTMENT_UPDATE"))
                .andExpect(jsonPath("$[0].isRead").value(false));
    }
}
