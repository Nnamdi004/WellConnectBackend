package com.alu.wellconnect.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "stories_reactions", uniqueConstraints = {
    @UniqueConstraint(name = "unique_story_user", columnNames = {"story_id", "user_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoryReaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reaction_id")
    private Long reactionId;

    @Column(name = "story_id", nullable = false)
    private Long storyId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "reaction_type", length = 50)
    private String reactionType;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
