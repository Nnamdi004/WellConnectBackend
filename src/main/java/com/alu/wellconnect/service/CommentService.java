package com.alu.wellconnect.service;

import com.alu.wellconnect.dto.CommentRequest;
import com.alu.wellconnect.dto.CommentResponse;
import com.alu.wellconnect.entity.Comment;
import com.alu.wellconnect.entity.NotificationType;
import com.alu.wellconnect.entity.Story;
import com.alu.wellconnect.entity.User;
import com.alu.wellconnect.repository.CommentRepository;
import com.alu.wellconnect.repository.StoryRepository;
import com.alu.wellconnect.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final StoryRepository storyRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Transactional
    public CommentResponse addComment(Long storyId, CommentRequest request, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Comment comment = Comment.builder()
                .storyId(storyId)
                .userId(user.getUserId())
                .content(request.getContent())
                .isAnonymous(request.getIsAnonymous())
                .build();

        Comment saved = commentRepository.save(comment);

        Story story = storyRepository.findById(storyId).orElse(null);
        if (story != null && !story.getUserId().equals(user.getUserId())) {
            User author = userRepository.findById(story.getUserId()).orElse(null);
            if (author != null) {
                String commenterName = request.getIsAnonymous() ? "Someone" : user.getUsername();
                notificationService.createNotification(
                        author,
                        NotificationType.NEW_COMMENT,
                        storyId,
                        commenterName + " recently commented on your story."
                );
            }
        }

        return mapToCommentResponse(saved, user);
    }

    public List<CommentResponse> getComments(Long storyId) {
        return commentRepository.findByStoryIdOrderByCreatedAtDesc(storyId).stream()
                .map(comment -> {
                    User user = userRepository.findById(comment.getUserId()).orElse(null);
                    return mapToCommentResponse(comment, user);
                })
                .collect(Collectors.toList());
    }

    private CommentResponse mapToCommentResponse(Comment comment, User user) {
        return CommentResponse.builder()
                .commentId(comment.getCommentId())
                .storyId(comment.getStoryId())
                .authorUsername(comment.getIsAnonymous() ? "Anonymous" : (user != null ? user.getUsername() : "Unknown"))
                .content(comment.getContent())
                .isAnonymous(comment.getIsAnonymous())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
