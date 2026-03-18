package com.alu.wellconnect.dto;

import com.alu.wellconnect.entity.ChatSessionStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class SessionResponse {
    private Long sessionId;
    private Long appointmentId;
    private ChatSessionStatus status;
    private Integer moodBefore;
    private Integer moodAfter;
    private LocalDateTime createdAt;
    private LocalDateTime closedAt;
}
