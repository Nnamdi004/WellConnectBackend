package com.alu.wellconnect.repository;

import com.alu.wellconnect.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByStoryIdOrderByCreatedAtDesc(Long storyId);
    long countByStoryId(Long storyId);
}
