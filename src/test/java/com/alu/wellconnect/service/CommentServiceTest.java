package com.alu.wellconnect.service;

import com.alu.wellconnect.dto.CommentRequest;
import com.alu.wellconnect.entity.Comment;
import com.alu.wellconnect.entity.NotificationType;
import com.alu.wellconnect.entity.Story;
import com.alu.wellconnect.entity.User;
import com.alu.wellconnect.repository.CommentRepository;
import com.alu.wellconnect.repository.StoryRepository;
import com.alu.wellconnect.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private StoryRepository storyRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private CommentService commentService;

    @Test
    public void testAddComment_TriggersNewCommentNotification() {
        Long storyId = 1L;
        String email = "test@example.com";
        CommentRequest request = new CommentRequest();
        request.setContent("Great progress!");
        request.setIsAnonymous(false);

        User currentUser = User.builder().userId(2L).username("commenter").email(email).build();
        User author = User.builder().userId(1L).username("author").email("author@example.com").build();
        Story story = Story.builder().storyId(storyId).userId(1L).title("My Journey").build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(currentUser));
        when(storyRepository.findById(storyId)).thenReturn(Optional.of(story));
        when(userRepository.findById(1L)).thenReturn(Optional.of(author));
        
        Comment savedComment = Comment.builder().commentId(10L).storyId(storyId).userId(2L).content("Great progress!").isAnonymous(false).build();
        when(commentRepository.save(any(Comment.class))).thenReturn(savedComment);

        // Act
        commentService.addComment(storyId, request, email);

        // Assert that a notification was created
        verify(notificationService, times(1)).createNotification(
                eq(author),
                eq(NotificationType.NEW_COMMENT),
                eq(storyId),
                contains("commenter recently commented")
        );
    }
}
