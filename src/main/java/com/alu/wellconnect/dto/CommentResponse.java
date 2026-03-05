package com.alu.wellconnect.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse {
    private Long commentId;
    private Long storyId;
    private String authorUsername;
    private String content;
    private Boolean isAnonymous;
    private LocalDateTime createdAt;
}
