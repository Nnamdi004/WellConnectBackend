package com.alu.wellconnect.repository;

import com.alu.wellconnect.entity.ContentReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContentReportRepository extends JpaRepository<ContentReport, Long> {
    List<ContentReport> findByStatus(ContentReport.ReportStatus status);
}
