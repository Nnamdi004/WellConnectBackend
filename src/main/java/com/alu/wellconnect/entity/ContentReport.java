package com.alu.wellconnect.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "content_report")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContentReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long reportId;

    @Column(name = "story_id")
    private Long storyId;

    @Column(name = "reported_by")
    private Long reportedBy;

    @Column(name = "reviewed_by")
    private Long reviewedBy;

    @Column(columnDefinition = "TEXT")
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportStatus status = ReportStatus.PENDING;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public enum ReportStatus {
        PENDING, REVIEWED, DISMISSED, ACTION_TAKEN
    }
}
