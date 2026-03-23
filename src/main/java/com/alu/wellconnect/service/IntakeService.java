package com.alu.wellconnect.service;

import com.alu.wellconnect.dto.IntakeRequest;
import com.alu.wellconnect.dto.IntakeResponse;
import com.alu.wellconnect.entity.IntakeQuestionnaire;
import com.alu.wellconnect.entity.User;
import com.alu.wellconnect.enums.SeverityLevel;
import com.alu.wellconnect.repository.IntakeQuestionnaireRepository;
import com.alu.wellconnect.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class IntakeService {

    private final IntakeQuestionnaireRepository intakeRepository;
    private final UserRepository userRepository;

    @Transactional
    public IntakeResponse submitIntake(String email, IntakeRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (intakeRepository.existsByUserId(user.getUserId())) {
            throw new RuntimeException("User already has an active intake questionnaire");
        }

        SeverityLevel severityLevel = calculateSeverityLevel(request.getPhq9Score(), request.getGad7Score());

        IntakeQuestionnaire intake = IntakeQuestionnaire.builder()
                .userId(user.getUserId())
                .phq9Score(request.getPhq9Score())
                .gad7Score(request.getGad7Score())
                .severityLevel(severityLevel)
                .build();

        IntakeQuestionnaire saved = intakeRepository.save(intake);
        return mapToResponse(saved);
    }

    public IntakeResponse getMyIntake(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        IntakeQuestionnaire intake = intakeRepository.findByUserId(user.getUserId())
                .orElseThrow(() -> new RuntimeException("No intake questionnaire found for this user"));

        return mapToResponse(intake);
    }

    /**
     * Scoring Engine: Calculates severity level based on PHQ-9 and GAD-7 scores
     * 
     * Rules:
     * - If either score >= 15 → SEVERE
     * - If either score >= 10 → MODERATE
     * - If either score >= 5 → MILD
     * - Else → MINIMAL
     */
    private SeverityLevel calculateSeverityLevel(Integer phq9Score, Integer gad7Score) {
        int totalScore = phq9Score + gad7Score;

        if (totalScore >= 15) {
            return SeverityLevel.SEVERE;
        } else if (totalScore >= 10) {
            return SeverityLevel.MODERATE;
        } else if (totalScore >= 4) {
            return SeverityLevel.MILD;
        } else {
            return SeverityLevel.MINIMAL;
        }
    }

    private IntakeResponse mapToResponse(IntakeQuestionnaire intake) {
        return IntakeResponse.builder()
                .intakeId(intake.getIntakeId())
                .userId(intake.getUserId())
                .phq9Score(intake.getPhq9Score())
                .gad7Score(intake.getGad7Score())
                .severityLevel(intake.getSeverityLevel())
                .submittedAt(intake.getSubmittedAt())
                .build();
    }
}
