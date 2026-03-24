package com.alu.wellconnect.repository;

import com.alu.wellconnect.entity.StoryReaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoryReactionRepository extends JpaRepository<StoryReaction, Long> {
    boolean existsByStoryIdAndUserId(Long storyId, Long userId);
    void deleteByStoryIdAndUserId(Long storyId, Long userId);
    long countByStoryId(Long storyId);
}
