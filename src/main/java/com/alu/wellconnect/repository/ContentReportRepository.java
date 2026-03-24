package com.alu.wellconnect.repository;

import com.alu.wellconnect.entity.ContentReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContentReportRepository extends JpaRepository<ContentReport, Long> {
    List<ContentReport> findByStatus(ContentReport.ReportStatus status);

    @Query("SELECT COUNT(r) FROM ContentReport r WHERE r.storyId IN " +
           "(SELECT s.storyId FROM Story s WHERE s.userId = :userId) " +
           "AND r.status = 'ACTION_TAKEN'")
    long countActionTakenReportsByUserId(@Param("userId") Long userId);
}
