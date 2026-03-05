package com.alu.wellconnect.dto;

import com.alu.wellconnect.enums.Visibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class StoryRequest {
    
    @NotNull(message = "Category ID is required")
    private Long categoryId;
    
    @NotBlank(message = "Title is required")
    private String title;
    
    @NotBlank(message = "Content is required")
    private String content;
    
    @NotNull(message = "isAnonymous is required")
    private Boolean isAnonymous;
    
    @NotNull(message = "Visibility is required")
    private Visibility visibility;
    
    private List<Long> tagIds;
}
