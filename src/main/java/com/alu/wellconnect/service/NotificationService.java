package com.alu.wellconnect.service;

import com.alu.wellconnect.dto.NotificationResponse;
import com.alu.wellconnect.entity.Notification;
import com.alu.wellconnect.entity.NotificationType;
import com.alu.wellconnect.entity.User;
import com.alu.wellconnect.repository.NotificationRepository;
import com.alu.wellconnect.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public void createNotification(User user, NotificationType type, Long referenceId, String message) {
        Notification notification = Notification.builder()
                .user(user)
                .type(type)
                .referenceId(referenceId)
                .message(message)
                .isRead(false)
                .build();
        notificationRepository.save(notification);
    }

    public List<NotificationResponse> getMyNotifications(String email) {
        User currUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return notificationRepository.findByUser_UserIdOrderByCreatedAtDesc(currUser.getUserId())
                .stream().map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public void markAsRead(Long notificationId, String email) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
                
        if (!notification.getUser().getEmail().equals(email)) {
             throw new RuntimeException("Not authorized to read this notification");
        }
        
        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    private NotificationResponse mapToResponse(Notification n) {
        return NotificationResponse.builder()
                .notificationId(n.getNotificationId())
                .type(n.getType())
                .referenceId(n.getReferenceId())
                .message(n.getMessage())
                .isRead(n.getIsRead())
                .createdAt(n.getCreatedAt())
                .build();
    }
}
