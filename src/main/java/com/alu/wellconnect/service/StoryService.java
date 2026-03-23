package com.alu.wellconnect.service;

import com.alu.wellconnect.dto.*;
import com.alu.wellconnect.entity.*;
import com.alu.wellconnect.enums.StoryStatus;
import com.alu.wellconnect.enums.Visibility;
import com.alu.wellconnect.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoryService {

    private final StoryRepository storyRepository;
    private final StoryCategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final NotificationService notificationService;

    @Transactional
    public StoryResponse createStory(StoryRequest request, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Story story = Story.builder()
                .userId(user.getUserId())
                .categoryId(request.getCategoryId())
                .title(request.getTitle())
                .content(request.getContent())
                .isAnonymous(request.getIsAnonymous())
                .visibility(request.getVisibility())
                .status(StoryStatus.PENDING)
                .tags(new HashSet<>())
                .build();

        if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            List<Tag> tags = tagRepository.findAllById(request.getTagIds());
            story.getTags().addAll(tags);
        }

        Story saved = storyRepository.save(story);
        return mapToResponse(saved);
    }

    public List<StoryResponse> getPublicFeed() {
        List<Story> stories = storyRepository.findByVisibilityAndStatus(
                Visibility.PUBLISHED, StoryStatus.APPROVED);
        return stories.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void likeStory(Long storyId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (likeRepository.existsByStoryIdAndUserId(storyId, user.getUserId())) {
            throw new RuntimeException("Already liked");
        }

        Like like = Like.builder()
                .storyId(storyId)
                .userId(user.getUserId())
                .build();

        likeRepository.save(like);
    }

    @Transactional
    public void unlikeStory(Long storyId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        likeRepository.deleteByStoryIdAndUserId(storyId, user.getUserId());
    }

    private StoryResponse mapToResponse(Story story) {
        User user = userRepository.findById(story.getUserId()).orElse(null);
        StoryCategory category = categoryRepository.findById(story.getCategoryId()).orElse(null);

        return StoryResponse.builder()
                .storyId(story.getStoryId())
                .authorUsername(story.getIsAnonymous() ? "Anonymous" : (user != null ? user.getUsername() : "Unknown"))
                .categoryId(story.getCategoryId())
                .categoryName(category != null ? category.getName() : null)
                .title(story.getTitle())
                .content(story.getContent())
                .isAnonymous(story.getIsAnonymous())
                .visibility(story.getVisibility())
                .status(story.getStatus())
                .tags(story.getTags().stream().map(Tag::getName).collect(Collectors.toList()))
                .likeCount(likeRepository.countByStoryId(story.getStoryId()))
                .commentCount(commentRepository.countByStoryId(story.getStoryId()))
                .createdAt(story.getCreatedAt())
                .updatedAt(story.getUpdatedAt())
                .build();
    }
}