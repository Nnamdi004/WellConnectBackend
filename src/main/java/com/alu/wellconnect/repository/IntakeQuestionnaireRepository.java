package com.alu.wellconnect.repository;

import com.alu.wellconnect.entity.IntakeQuestionnaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IntakeQuestionnaireRepository extends JpaRepository<IntakeQuestionnaire, Long> {
    
    Optional<IntakeQuestionnaire> findByUserId(Long userId);
    
    boolean existsByUserId(Long userId);
}
