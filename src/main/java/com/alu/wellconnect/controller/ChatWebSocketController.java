package com.alu.wellconnect.controller;

import com.alu.wellconnect.dto.ChatMessagePayload;
import com.alu.wellconnect.entity.ChatMessage;
import com.alu.wellconnect.entity.ChatSession;
import com.alu.wellconnect.entity.SenderRole;
import com.alu.wellconnect.repository.ChatMessageRepository;
import com.alu.wellconnect.repository.ChatSessionRepository;
import com.alu.wellconnect.repository.TherapistRepository;
import com.alu.wellconnect.repository.UserRepository;
import com.alu.wellconnect.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatSessionRepository chatSessionRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final TherapistRepository therapistRepository;
    private final JwtUtil jwtUtil;

    @MessageMapping("/chat/{sessionId}/sendMessage")
    public void sendMessage(@DestinationVariable Long sessionId, @Payload ChatMessagePayload payload) {
        String token = payload.getToken();

        if (token == null || !jwtUtil.validateToken(token)) {
            throw new RuntimeException("Invalid or missing token");
        }

        String email = jwtUtil.extractEmail(token);
        String roleStr = jwtUtil.extractRole(token);
        SenderRole role;
        try {
            role = SenderRole.valueOf(roleStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            role = SenderRole.USER;
        }

        Long senderId;
        if (role == SenderRole.THERAPIST) {
            senderId = therapistRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Therapist not found"))
                    .getTherapistId();
        } else {
            senderId = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"))
                    .getUserId();
        }

        ChatSession session = chatSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Chat session not found"));

        ChatMessage chatMessage = ChatMessage.builder()
                .session(session)
                .senderId(senderId)
                .senderRole(role)
                .content(payload.getContent())
                .build();

        chatMessage = chatMessageRepository.save(chatMessage);

        messagingTemplate.convertAndSend("/topic/session/" + sessionId, chatMessage);
    }
}
