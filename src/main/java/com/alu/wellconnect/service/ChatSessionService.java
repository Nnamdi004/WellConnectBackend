package com.alu.wellconnect.service;

import com.alu.wellconnect.dto.SessionCloseRequest;
import com.alu.wellconnect.dto.SessionResponse;
import com.alu.wellconnect.dto.SessionStartRequest;
import com.alu.wellconnect.entity.Appointment;
import com.alu.wellconnect.entity.AppointmentStatus;
import com.alu.wellconnect.entity.ChatSession;
import com.alu.wellconnect.entity.ChatSessionStatus;
import com.alu.wellconnect.repository.AppointmentRepository;
import com.alu.wellconnect.repository.ChatSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ChatSessionService {

    private final ChatSessionRepository chatSessionRepository;
    private final AppointmentRepository appointmentRepository;

    @Transactional
    public Long startSession(Long appointmentId, SessionStartRequest request) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        if (appointment.getStatus() != AppointmentStatus.CONFIRMED) {
            throw new RuntimeException("Cannot start session for unconfirmed appointment");
        }

        if (chatSessionRepository.findByAppointment_AppointmentId(appointmentId).isPresent()) {
            throw new RuntimeException("Session already exists for this appointment");
        }

        ChatSession session = ChatSession.builder()
                .appointment(appointment)
                .status(ChatSessionStatus.ACTIVE)
                .moodBefore(request.getMoodBefore())
                .build();

        session = chatSessionRepository.save(session);
        return session.getSessionId();
    }

    @Transactional
    public SessionResponse closeSession(Long sessionId, SessionCloseRequest request) {
        ChatSession session = chatSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Chat session not found"));

        if (session.getStatus() == ChatSessionStatus.CLOSED) {
            throw new RuntimeException("Chat session is already closed");
        }

        session.setStatus(ChatSessionStatus.CLOSED);
        session.setMoodAfter(request.getMoodAfter());
        session.setClosedAt(LocalDateTime.now());
        session = chatSessionRepository.save(session);

        return SessionResponse.builder()
                .sessionId(session.getSessionId())
                .appointmentId(session.getAppointment().getAppointmentId())
                .status(session.getStatus())
                .moodBefore(session.getMoodBefore())
                .moodAfter(session.getMoodAfter())
                .createdAt(session.getCreatedAt())
                .closedAt(session.getClosedAt())
                .build();
    }
}
