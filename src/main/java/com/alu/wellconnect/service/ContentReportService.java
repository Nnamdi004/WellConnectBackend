package com.alu.wellconnect.service;

import com.alu.wellconnect.dto.CreateReportRequest;
import com.alu.wellconnect.dto.UpdateReportRequest;
import com.alu.wellconnect.entity.ContentReport;
import com.alu.wellconnect.repository.ContentReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContentReportService {

    private final ContentReportRepository contentReportRepository;

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
}
