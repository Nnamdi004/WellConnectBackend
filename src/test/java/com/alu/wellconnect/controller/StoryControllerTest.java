package com.alu.wellconnect.controller;

import com.alu.wellconnect.dto.StoryRequest;
import com.alu.wellconnect.dto.StoryResponse;
import com.alu.wellconnect.enums.Visibility;
import com.alu.wellconnect.security.JwtService;
import com.alu.wellconnect.service.StoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false) // Ignore JWT filter for simplicity in endpoint test
public class StoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StoryService storyService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    public void testCreateStory() throws Exception {
        StoryRequest request = new StoryRequest();
        request.setTitle("Test Story");
        request.setContent("This is a test.");
        request.setCategoryId(1L);
        request.setVisibility(Visibility.PUBLISHED);
        request.setIsAnonymous(false);

        StoryResponse response = StoryResponse.builder().storyId(1L).title("Test Story").build();
        when(storyService.createStory(any(StoryRequest.class), any())).thenReturn(response);

        mockMvc.perform(post("/api/stories")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    public void testLikeStory() throws Exception {
        mockMvc.perform(post("/api/stories/1/like").with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    public void testUnlikeStory() throws Exception {
        mockMvc.perform(delete("/api/stories/1/like").with(csrf()))
                .andExpect(status().isNoContent());
    }
}
