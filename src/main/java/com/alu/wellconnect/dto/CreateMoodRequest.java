package com.alu.wellconnect.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateMoodRequest {

    @NotBlank(message = "Mood label is required")
    private String moodLabel;

    @NotNull(message = "Mood score is required")
    @Min(value = 1, message = "Mood score must be between 1 and 10")
    @Max(value = 10, message = "Mood score must be between 1 and 10")
    private Integer moodScore;

    private String notes;
}
