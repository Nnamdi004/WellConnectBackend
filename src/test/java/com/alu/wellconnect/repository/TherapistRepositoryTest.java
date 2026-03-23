package com.alu.wellconnect.repository;

import com.alu.wellconnect.entity.Therapist;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class TherapistRepositoryTest {

    @Autowired
    private TherapistRepository therapistRepository;

    @Autowired
    private TestEntityManager entityManager;

    // --- TEST 2: The Specialty Query ---
    @Test
    public void testFindBySpecialty_OnlyReturnsTherapists() {
        // Arrange
        Therapist t1 = Therapist.builder()
                .fullName("Dr. Trauma")
                .email("trauma@wellconnect.com")
                .passwordHash("hashed")
                .specialisation("Trauma")
                .build();
        entityManager.persist(t1);

        Therapist t2 = Therapist.builder()
                .fullName("Dr. Anxiety")
                .email("anxiety@wellconnect.com")
                .passwordHash("hashed")
                .specialisation("Anxiety")
                .build();
        entityManager.persist(t2);

        // Act
        List<Therapist> traumaTherapists = therapistRepository.findBySpecialisation("Trauma");

        // Assert
        assertNotNull(traumaTherapists);
        assertEquals(1, traumaTherapists.size());
        for (Therapist t : traumaTherapists) {
            assertEquals("Trauma", t.getSpecialisation());
        }
    }
}
