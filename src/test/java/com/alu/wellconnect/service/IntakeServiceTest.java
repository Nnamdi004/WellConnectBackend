package com.alu.wellconnect.service;

import com.alu.wellconnect.dto.IntakeRequest;
import com.alu.wellconnect.enums.SeverityLevel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class IntakeServiceTest {

    @InjectMocks
    private IntakeService intakeService;

    @Test
    public void testCalculateSeverity_SevereScore_Total16() {
        IntakeRequest form = new IntakeRequest();
        form.setPhq9Score(10);
        form.setGad7Score(6); // Total = 16
        
        SeverityLevel result = intakeService.calculateSeverity(form);
        assertEquals(SeverityLevel.SEVERE, result, "Score of 16 should return SEVERE");
    }

    @Test
    public void testCalculateSeverity_MildScore_Total4() {
        IntakeRequest form = new IntakeRequest();
        form.setPhq9Score(2);
        form.setGad7Score(2); // Total = 4
        
        SeverityLevel result = intakeService.calculateSeverity(form);
        assertEquals(SeverityLevel.MILD, result, "Score of 4 should return MILD");
    }

    @Test
    public void testCalculateSeverity_BoundaryScores() {
        IntakeRequest form10 = new IntakeRequest();
        form10.setPhq9Score(5);
        form10.setGad7Score(5); // Total = 10 (Testing the boundary)
        
        SeverityLevel result10 = intakeService.calculateSeverity(form10);
        assertEquals(SeverityLevel.MODERATE, result10, "Score of exactly 10 should return MODERATE");
    }
}
