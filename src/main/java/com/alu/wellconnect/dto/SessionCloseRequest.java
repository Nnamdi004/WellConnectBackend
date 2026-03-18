package com.alu.wellconnect.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SessionCloseRequest {

    @NotNull(message = "mood_after is required")
    @Min(value = 1, message = "Mood must be at least 1")
    @Max(value = 10, message = "Mood must be at most 10")
    private Integer moodAfter;

}
