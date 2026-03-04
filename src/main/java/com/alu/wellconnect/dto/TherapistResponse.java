package com.alu.wellconnect.dto;

import com.alu.wellconnect.entity.Therapist;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TherapistResponse {
    private Long therapistId;
    private String fullName;
    private String email;
    private String specialisation;
    private Therapist.Status status;
    private LocalDateTime createdAt;
}
