package com.alu.wellconnect.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CommentRequest {
    
    @NotBlank(message = "Content is required")
    private String content;
    
    @NotNull(message = "isAnonymous is required")
    private Boolean isAnonymous;
}
