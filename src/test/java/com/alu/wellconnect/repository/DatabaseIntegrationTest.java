package com.alu.wellconnect.repository;

import com.alu.wellconnect.entity.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class DatabaseIntegrationTest {

    @Autowired
    private TestEntityManager entityManager; // Used to insert dummy data safely

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private StoryRepository storyRepository;

    @Autowired
    private UserRepository userRepository;

    // --- TEST 1: The Notification Chronology Query ---
    @Test
    public void testFindByUserIdOrderByCreatedAtDesc() throws InterruptedException {
        User user = User.builder()
                .username("test_user")
                .email("test@alu.edu")
                .passwordHash("hashed")
                .build();
        user = entityManager.persist(user);

        // Act: Save 3 notifications with a slight delay so timestamps are different
        Notification n1 = Notification.builder()
                .user(user)
                .type(NotificationType.SYSTEM_ALERT)
                .message("First")
                .isRead(false)
                .build();
        entityManager.persist(n1);
        Thread.sleep(10); 
        
        Notification n2 = Notification.builder()
                .user(user)
                .type(NotificationType.NEW_COMMENT)
                .message("Second")
                .isRead(false)
                .build();
        entityManager.persist(n2);
        Thread.sleep(10);
        
        Notification n3 = Notification.builder()
                .user(user)
                .type(NotificationType.APPOINTMENT_UPDATE)
                .message("Third")
                .isRead(false)
                .build();
        entityManager.persist(n3);

        List<Notification> results = notificationRepository.findByUser_UserIdOrderByCreatedAtDesc(user.getUserId());

        // Assert: Ensure it brings back the newest one ("Third") first!
        assertEquals(3, results.size());
        assertEquals("Third", results.get(0).getMessage());
        assertEquals("Second", results.get(1).getMessage());
        assertEquals("First", results.get(2).getMessage());
    }

    // --- TEST 3: The Cascade / User Deletion Test ---
    @Test
    public void testUserDeletion_CascadesToStories() {
        // Arrange: Create a user and a story linked to that user
        User user = User.builder()
                .username("deleteMe")
                .email("deleteMe@alu.edu")
                .passwordHash("password")
                .role(User.Role.USER)
                .build();
        user = entityManager.persist(user);

        Story story = Story.builder()
                .userId(user.getUserId())
                .title("My Journey")
                .content("Content here")
                .build();
        story = entityManager.persist(story);

        // Act: Delete the user
        userRepository.delete(user);
        userRepository.flush(); // Force the delete to the database immediately

        // Assert: Ensure the story was automatically deleted (Cascaded)
        Optional<Story> deletedStory = storyRepository.findById(story.getStoryId());
        assertTrue(deletedStory.isEmpty(), "Story should be deleted when user is deleted");
    }
}
