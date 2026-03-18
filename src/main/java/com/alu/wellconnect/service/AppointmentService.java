package com.alu.wellconnect.service;

import com.alu.wellconnect.entity.Appointment;
import com.alu.wellconnect.entity.AppointmentStatus;
import com.alu.wellconnect.entity.NotificationType;
import com.alu.wellconnect.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final NotificationService notificationService;

    @Transactional
    public Appointment updateStatus(Long appointmentId, AppointmentStatus newStatus) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
                
        appointment.setStatus(newStatus);
        Appointment saved = appointmentRepository.save(appointment);
        
        if (newStatus == AppointmentStatus.CONFIRMED && saved.getUser() != null) {
            notificationService.createNotification(
                    saved.getUser(),
                    NotificationType.APPOINTMENT_UPDATE,
                    appointmentId,
                    "Your appointment has been confirmed."
            );
        }
        
        return saved;
    }
}
