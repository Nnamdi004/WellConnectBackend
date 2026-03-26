package com.alu.wellconnect.service;

import com.alu.wellconnect.entity.Appointment;
import com.alu.wellconnect.entity.Therapist;
import com.alu.wellconnect.repository.AppointmentRepository;
import com.alu.wellconnect.repository.TherapistRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private TherapistRepository therapistRepository;

    @InjectMocks
    private AppointmentService appointmentService;

    @Test
    public void testGetAvailableSlots_FiltersOutBookedTimes() {
        Long therapistId = 1L;
        String date = "2026-03-25";

        // Mock therapist standard hours
        Therapist therapist = new Therapist();
        therapist.setStandardHours("09:00-17:00");
        when(therapistRepository.findById(therapistId)).thenReturn(Optional.of(therapist));

        // Mock booked appointments
        Appointment appt1 = new Appointment(); 
        appt1.setScheduledTime(java.time.LocalDateTime.parse("2026-03-25T10:00:00"));
        Appointment appt2 = new Appointment(); 
        appt2.setScheduledTime(java.time.LocalDateTime.parse("2026-03-25T14:00:00"));
        
        when(appointmentRepository.findByTherapistIdAndDate(eq(therapistId), eq(java.time.LocalDate.parse(date))))
            .thenReturn(Arrays.asList(appt1, appt2));

        // Act
        List<String> availableSlots = appointmentService.getAvailableSlots(therapistId, date);

        // Assert
        assertFalse(availableSlots.contains("10:00"), "10:00 should be filtered out");
        assertFalse(availableSlots.contains("14:00"), "14:00 should be filtered out");
        assertTrue(availableSlots.contains("09:00"), "09:00 should be available");
        assertTrue(availableSlots.contains("11:00"), "11:00 should be available");
    }
}
