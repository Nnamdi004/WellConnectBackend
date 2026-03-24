package com.alu.wellconnect.repository;

import com.alu.wellconnect.entity.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {

    Optional<ChatSession> findByAppointment_AppointmentId(Long appointmentId);
}
