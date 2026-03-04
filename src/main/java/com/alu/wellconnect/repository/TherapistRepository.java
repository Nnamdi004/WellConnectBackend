package com.alu.wellconnect.repository;

import com.alu.wellconnect.entity.Therapist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TherapistRepository extends JpaRepository<Therapist, Long> {
    Optional<Therapist> findByEmail(String email);
    boolean existsByEmail(String email);
}
