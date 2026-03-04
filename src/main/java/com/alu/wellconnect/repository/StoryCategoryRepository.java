package com.alu.wellconnect.repository;

import com.alu.wellconnect.entity.StoryCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoryCategoryRepository extends JpaRepository<StoryCategory, Long> {
}
