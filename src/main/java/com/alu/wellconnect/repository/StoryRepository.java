package com.alu.wellconnect.repository;

import com.alu.wellconnect.entity.Story;
import com.alu.wellconnect.enums.StoryStatus;
import com.alu.wellconnect.enums.Visibility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StoryRepository extends JpaRepository<Story, Long> {
    
    @Query("SELECT s FROM Story s WHERE s.visibility = :visibility AND s.status = :status ORDER BY s.createdAt DESC")
    List<Story> findByVisibilityAndStatus(Visibility visibility, StoryStatus status);
}
