package com.alu.wellconnect.service;

import com.alu.wellconnect.dto.CreateReportRequest;
import com.alu.wellconnect.dto.UpdateReportRequest;
import com.alu.wellconnect.entity.ContentReport;
import com.alu.wellconnect.entity.Story;
import com.alu.wellconnect.entity.User;
import com.alu.wellconnect.enums.StoryStatus;
import com.alu.wellconnect.repository.ContentReportRepository;
import com.alu.wellconnect.repository.StoryRepository;
import com.alu.wellconnect.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContentReportService {

    private final ContentReportRepository contentReportRepository;
    private final StoryRepository storyRepository;
    private final UserRepository userRepository;

    private static final int SUSPENSION_THRESHOLD = 2;

    public ContentReport createReport(CreateReportRequest request, Long userId) {
        ContentReport report = ContentReport.builder()
                .storyId(request.getStoryId())
                .reportedBy(userId)
                .reason(request.getReason())
                .status(ContentReport.ReportStatus.PENDING)
                .build();

        return contentReportRepository.save(report);
    }

    public List<ContentReport> getPendingReports() {
        return contentReportRepository.findByStatus(ContentReport.ReportStatus.PENDING);
    }

    public ContentReport updateReportStatus(Long reportId, UpdateReportRequest request, Long adminId) {
        ContentReport report = contentReportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        report.setStatus(ContentReport.ReportStatus.valueOf(request.getStatus()));
        report.setReviewedBy(adminId);

        return contentReportRepository.save(report);
    }

    @Transactional
    public ContentReport resolveReport(Long reportId, Long adminId) {
        // 1. Fetch and validate the report
        ContentReport report = contentReportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        // 2. Mark report as ACTION_TAKEN
        report.setStatus(ContentReport.ReportStatus.ACTION_TAKEN);
        report.setReviewedBy(adminId);
        ContentReport savedReport = contentReportRepository.save(report);

        // 3. Mark the offending story as REMOVED
        Story story = storyRepository.findById(report.getStoryId())
                .orElseThrow(() -> new RuntimeException("Story not found for report"));

        story.setStatus(StoryStatus.REMOVED);
        storyRepository.save(story);

        // 4. (Optional) Auto-suspend user if they have multiple resolved reports
        long resolvedCount = contentReportRepository.countActionTakenReportsByUserId(story.getUserId());
        if (resolvedCount >= SUSPENSION_THRESHOLD) {
            userRepository.findById(story.getUserId()).ifPresent(user -> {
                user.setStatus(User.Status.SUSPENDED);
                userRepository.save(user);
            });
        }

        return savedReport;
    }
}

