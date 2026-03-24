package com.alu.wellconnect.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(roles = "USER") // Simulates a logged-in Patient/User
    public void testUserAccessingTherapistEndpoint_Returns403() throws Exception {
        mockMvc.perform(get("/api/therapists/appointments"))
               .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "THERAPIST") // Simulates a logged-in Therapist
    public void testTherapistAccessingAdminEndpoint_Returns403() throws Exception {
        mockMvc.perform(put("/api/admin/reports/1/resolve"))
               .andExpect(status().isForbidden());
    }
}
