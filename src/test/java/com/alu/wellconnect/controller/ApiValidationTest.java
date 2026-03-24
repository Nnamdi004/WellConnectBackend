package com.alu.wellconnect.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ApiValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(roles = "USER")
    public void testPostAppointment_PastDate_Returns400() throws Exception {
        // A date safely in the past to trigger validation failure
        String pastAppointmentJson = "{ \"scheduledTime\": \"2020-05-10T10:00:00\", \"therapistId\": 1 }";
        
        mockMvc.perform(post("/api/appointments")
               .contentType(MediaType.APPLICATION_JSON)
               .content(pastAppointmentJson))
               .andExpect(status().isBadRequest()); // Expect 400 Bad Request
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testGetStory_NotFound_Returns404() throws Exception {
        // ID 99999 is highly unlikely to exist
        mockMvc.perform(get("/api/stories/99999"))
               .andExpect(status().isNotFound()); // Expect clean 404
    }
}
