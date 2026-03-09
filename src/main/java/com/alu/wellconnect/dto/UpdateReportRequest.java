package com.alu.wellconnect.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateReportRequest {

    @NotNull(message = "Status is required")
    private String status;
}
