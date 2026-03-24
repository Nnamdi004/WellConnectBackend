package com.alu.wellconnect.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class IntakeRequest {
    
    @NotNull(message = "PHQ-9 score is required")
    @Min(value = 0, message = "PHQ-9 score must be between 0 and 27")
    @Max(value = 27, message = "PHQ-9 score must be between 0 and 27")
    private Integer phq9Score;
    
    @NotNull(message = "GAD-7 score is required")
    @Min(value = 0, message = "GAD-7 score must be between 0 and 21")
    @Max(value = 21, message = "GAD-7 score must be between 0 and 21")
    private Integer gad7Score;
}
