package com.alu.wellconnect.dto;

import com.alu.wellconnect.enums.StoryStatus;
import com.alu.wellconnect.enums.Visibility;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoryResponse {
    private Long storyId;
    private String authorUsername;
    private Long categoryId;
    private String categoryName;
    private String title;
    private String content;
    private Boolean isAnonymous;
    private Visibility visibility;
    private StoryStatus status;
    private List<String> tags;
    private Long likeCount;
    private Long commentCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
