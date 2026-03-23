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
    private final com.alu.wellconnect.repository.TherapistRepository therapistRepository;
    private final com.alu.wellconnect.repository.UserRepository userRepository;

    public com.alu.wellconnect.entity.User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public java.util.List<String> getAvailability(Long therapistId, java.time.LocalDate date) {
        com.alu.wellconnect.entity.Therapist therapist = therapistRepository.findById(therapistId)
                .orElseThrow(() -> new RuntimeException("Therapist not found"));

        String hours = therapist.getDailyAvailableHours();
        if (hours == null || hours.isBlank()) {
            hours = "09:00-17:00";
        }

        String[] parts = hours.split("-");
        java.time.LocalTime startTime = java.time.LocalTime.parse(parts[0]);
        java.time.LocalTime endTime = java.time.LocalTime.parse(parts[1]);

        java.util.List<String> allSlots = new java.util.ArrayList<>();
        java.time.LocalTime current = startTime;
        while (current.isBefore(endTime)) {
            allSlots.add(current.toString());
            current = current.plusHours(1);
        }

        java.util.List<Appointment> existingAppointments = appointmentRepository.findAppointmentsByTherapistAndDate(
                therapistId,
                date,
                java.util.List.of(com.alu.wellconnect.entity.AppointmentStatus.PENDING, com.alu.wellconnect.entity.AppointmentStatus.CONFIRMED));

        java.util.List<String> bookedTimes = existingAppointments.stream()
                .map(a -> a.getScheduledTime().toLocalTime().toString())
                .collect(java.util.stream.Collectors.toList());

        allSlots.removeAll(bookedTimes);
        return allSlots;
    }

    @Transactional
    public Appointment bookAppointment(Long userId, Long therapistId, java.time.LocalDateTime scheduledTime) {
        com.alu.wellconnect.entity.User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        com.alu.wellconnect.entity.Therapist therapist = therapistRepository.findById(therapistId)
                .orElseThrow(() -> new RuntimeException("Therapist not found"));

        Appointment appointment = Appointment.builder()
                .user(user)
                .therapist(therapist)
                .scheduledTime(scheduledTime)
                .status(AppointmentStatus.PENDING)
                .build();

        Appointment saved = appointmentRepository.save(appointment);

        notificationService.createNotification(
                user,
                com.alu.wellconnect.entity.NotificationType.APPOINTMENT_UPDATE,
                saved.getAppointmentId(),
                "Your appointment with " + therapist.getFullName() + " has been booked for " + scheduledTime
        );

        return saved;
    }

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
