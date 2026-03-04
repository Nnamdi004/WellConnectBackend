package com.alu.wellconnect.dto;

import com.alu.wellconnect.entity.Therapist;
import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class TherapistUpdateRequest {
    
    private String fullName;
    
    @Email(message = "Invalid email format")
    private String email;
    
    private String specialisation;
    
    private Therapist.Status status;
}
