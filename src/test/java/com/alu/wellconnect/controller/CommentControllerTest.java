package com.alu.wellconnect.controller;

import com.alu.wellconnect.dto.CommentRequest;
import com.alu.wellconnect.dto.CommentResponse;
import com.alu.wellconnect.service.CommentService;
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
@AutoConfigureMockMvc(addFilters = false)
public class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentService commentService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    public void testAddComment() throws Exception {
        CommentRequest request = new CommentRequest();
        request.setContent("Great story!");
        request.setIsAnonymous(false);

        CommentResponse response = CommentResponse.builder().commentId(1L).content("Great story!").build();
        when(commentService.addComment(eq(1L), any(CommentRequest.class), any())).thenReturn(response);

        mockMvc.perform(post("/api/stories/1/comments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    public void testGetComments() throws Exception {
        when(commentService.getComments(1L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/stories/1/comments"))
                .andExpect(status().isOk());
    }
}
