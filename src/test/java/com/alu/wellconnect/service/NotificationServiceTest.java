package com.alu.wellconnect.service;

import com.alu.wellconnect.dto.CommentRequest;
import com.alu.wellconnect.entity.NotificationType;
import com.alu.wellconnect.entity.Story;
import com.alu.wellconnect.entity.User;
import com.alu.wellconnect.repository.CommentRepository;
import com.alu.wellconnect.repository.NotificationRepository;
import com.alu.wellconnect.repository.StoryRepository;
import com.alu.wellconnect.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private StoryRepository storyRepository;

    @Mock
    private UserRepository userRepository;

    private NotificationService notificationService;
    private CommentService commentService;

    @BeforeEach
    void setUp() {
        notificationService = new NotificationService(notificationRepository, userRepository);
        commentService = new CommentService(commentRepository, storyRepository, userRepository, notificationService);
    }

    @Test
    void TestA_AddComment_TriggersNotificationSave() {
        // Mock data
        User author = User.builder().userId(1L).email("author@example.com").username("AuthorUser").build();
        User commenter = User.builder().userId(2L).email("commenter@example.com").username("CommenterUser").build();
        Story story = Story.builder().storyId(10L).userId(1L).build();
        
        CommentRequest request = new CommentRequest();
        request.setContent("Great story!");
        request.setIsAnonymous(false);

        when(userRepository.findByEmail("commenter@example.com")).thenReturn(Optional.of(commenter));
        when(storyRepository.findById(10L)).thenReturn(Optional.of(story));
        when(userRepository.findById(1L)).thenReturn(Optional.of(author));
        when(commentRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        // Call addComment
        commentService.addComment(10L, request, "commenter@example.com");

        // Verify that notificationRepository.save() is called exactly one time
        verify(notificationRepository, times(1)).save(any());
        
        // Also verify the type is NEW_COMMENT as requested
        verify(notificationRepository).save(argThat(n -> n.getType() == NotificationType.NEW_COMMENT));
    }
}
