package com.alu.wellconnect.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TagRequest {
    
    @NotBlank(message = "Name is required")
    private String name;
}
