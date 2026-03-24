package com.alu.wellconnect.dto;

import com.alu.wellconnect.enums.SeverityLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IntakeResponse {
    private Long intakeId;
    private Long userId;
    private Integer phq9Score;
    private Integer gad7Score;
    private SeverityLevel severityLevel;
    private LocalDateTime submittedAt;
}
