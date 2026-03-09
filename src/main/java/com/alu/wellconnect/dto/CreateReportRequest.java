package com.alu.wellconnect.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateReportRequest {

    @NotNull(message = "Story ID is required")
    private Long storyId;

    @NotBlank(message = "Reason is required")
    private String reason;
}
