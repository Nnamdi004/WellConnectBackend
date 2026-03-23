package com.alu.wellconnect;

import com.alu.wellconnect.controller.AppointmentController;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void TestA_Forbidden_UserAccessTherapistsAppointments() throws Exception {
        mockMvc.perform(get("/api/therapists/appointments")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void TestB_Forbidden_TherapistAccessAdminResolve() throws Exception {
        mockMvc.perform(put("/api/admin/reports/1/resolve")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_THERAPIST"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void TestA_BadRequest_PastAppointmentDate() throws Exception {
        AppointmentController.AppointmentRequest request = new AppointmentController.AppointmentRequest();
        request.setTherapistId(1L);
        request.setScheduledTime(LocalDateTime.now().minusDays(1));

        mockMvc.perform(post("/api/appointments")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void TestA_NotFound_NonExistentStory() throws Exception {
        mockMvc.perform(get("/api/stories/999999")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isNotFound());
    }
}
