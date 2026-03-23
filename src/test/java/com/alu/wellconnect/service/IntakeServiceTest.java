package com.alu.wellconnect.service;

import com.alu.wellconnect.dto.IntakeRequest;
import com.alu.wellconnect.dto.IntakeResponse;
import com.alu.wellconnect.entity.IntakeQuestionnaire;
import com.alu.wellconnect.entity.User;
import com.alu.wellconnect.enums.SeverityLevel;
import com.alu.wellconnect.repository.IntakeQuestionnaireRepository;
import com.alu.wellconnect.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class IntakeServiceTest {

    @Mock
    private IntakeQuestionnaireRepository intakeRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private IntakeService intakeService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .userId(1L)
                .email("test@example.com")
                .build();
    }

    @Test
    void TestA_SevereScore_Total16() {
        // PHQ-9 + GAD-7 = 16 (e.g. 10 + 6)
        IntakeRequest request = new IntakeRequest();
        request.setPhq9Score(10);
        request.setGad7Score(6);

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(intakeRepository.existsByUserId(1L)).thenReturn(false);
        when(intakeRepository.save(any(IntakeQuestionnaire.class))).thenAnswer(i -> i.getArguments()[0]);

        IntakeResponse response = intakeService.submitIntake("test@example.com", request);

        assertEquals(SeverityLevel.SEVERE, response.getSeverityLevel());
    }

    @Test
    void TestB_MildScore_Total4() {
        // PHQ-9 + GAD-7 = 4 (e.g. 2 + 2)
        IntakeRequest request = new IntakeRequest();
        request.setPhq9Score(2);
        request.setGad7Score(2);

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(intakeRepository.existsByUserId(1L)).thenReturn(false);
        when(intakeRepository.save(any(IntakeQuestionnaire.class))).thenAnswer(i -> i.getArguments()[0]);

        IntakeResponse response = intakeService.submitIntake("test@example.com", request);

        assertEquals(SeverityLevel.MILD, response.getSeverityLevel());
    }

    @Test
    void TestC_BoundaryNumbers_10And15() {
        // Exactly 10 -> MODERATE
        IntakeRequest request10 = new IntakeRequest();
        request10.setPhq9Score(5);
        request10.setGad7Score(5);

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(intakeRepository.existsByUserId(1L)).thenReturn(false);
        when(intakeRepository.save(any(IntakeQuestionnaire.class))).thenAnswer(i -> i.getArguments()[0]);

        IntakeResponse response10 = intakeService.submitIntake("test@example.com", request10);
        assertEquals(SeverityLevel.MODERATE, response10.getSeverityLevel());

        // Exactly 15 -> SEVERE
        IntakeRequest request15 = new IntakeRequest();
        request15.setPhq9Score(8);
        request15.setGad7Score(7);

        IntakeResponse response15 = intakeService.submitIntake("test@example.com", request15);
        assertEquals(SeverityLevel.SEVERE, response15.getSeverityLevel());
    }
}
