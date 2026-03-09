package com.alu.wellconnect.service;

import com.alu.wellconnect.dto.CreateMoodRequest;
import com.alu.wellconnect.entity.MoodLog;
import com.alu.wellconnect.repository.MoodLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MoodLogService {

    private final MoodLogRepository moodLogRepository;

    public MoodLog createMoodLog(CreateMoodRequest request, Long userId) {
        MoodLog moodLog = MoodLog.builder()
                .userId(userId)
                .moodLabel(request.getMoodLabel())
                .moodScore(request.getMoodScore())
                .notes(request.getNotes())
                .build();

        return moodLogRepository.save(moodLog);
    }

    public List<MoodLog> getUserMoodHistory(Long userId) {
        return moodLogRepository.findByUserIdOrderByLoggedAtDesc(userId);
    }
}
