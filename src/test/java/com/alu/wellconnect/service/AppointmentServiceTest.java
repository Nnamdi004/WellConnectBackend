package com.alu.wellconnect.service;

import com.alu.wellconnect.entity.Appointment;
import com.alu.wellconnect.entity.AppointmentStatus;
import com.alu.wellconnect.entity.Therapist;
import com.alu.wellconnect.repository.AppointmentRepository;
import com.alu.wellconnect.repository.TherapistRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private TherapistRepository therapistRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private AppointmentService appointmentService;

    @Test
    void TestA_TherapistAvailability_FiltersBookedSlots() {
        // Mock therapist with 09:00-17:00
        Therapist therapist = Therapist.builder()
                .therapistId(1L)
                .dailyAvailableHours("09:00-17:00")
                .build();

        LocalDate date = LocalDate.now();

        // Mock existing appointments at 10:00 and 14:00
        Appointment appt1 = Appointment.builder()
                .scheduledTime(LocalDateTime.of(date, LocalTime.of(10, 0)))
                .build();
        Appointment appt2 = Appointment.builder()
                .scheduledTime(LocalDateTime.of(date, LocalTime.of(14, 0)))
                .build();

        when(therapistRepository.findById(1L)).thenReturn(Optional.of(therapist));
        when(appointmentRepository.findAppointmentsByTherapistAndDate(eq(1L), eq(date), any()))
                .thenReturn(List.of(appt1, appt2));

        List<String> availability = appointmentService.getAvailability(1L, date);

        // Assert 10:00 and 14:00 are filtered out
        assertFalse(availability.contains("10:00"));
        assertFalse(availability.contains("14:00"));
        
        // Assert other slots are present
        assertTrue(availability.contains("09:00"));
        assertTrue(availability.contains("11:00"));
        assertTrue(availability.contains("13:00"));
        assertTrue(availability.contains("16:00"));
    }
}
