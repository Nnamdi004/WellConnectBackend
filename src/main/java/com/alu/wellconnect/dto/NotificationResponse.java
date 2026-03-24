package com.alu.wellconnect.dto;

import com.alu.wellconnect.entity.NotificationType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class NotificationResponse {
    private Long notificationId;
    private NotificationType type;
    private Long referenceId;
    private String message;
    private Boolean isRead;
    private LocalDateTime createdAt;
}
