package com.alu.wellconnect.repository;

import com.alu.wellconnect.entity.Comment;
import com.alu.wellconnect.entity.Notification;
import com.alu.wellconnect.entity.NotificationType;
import com.alu.wellconnect.entity.Story;
import com.alu.wellconnect.entity.Therapist;
import com.alu.wellconnect.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class DatabaseIntegrityTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private TherapistRepository therapistRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StoryRepository storyRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Test
    void TestNotificationQuery_Ordering() {
        User user = User.builder().username("test").passwordHash("hash").build();
        entityManager.persist(user);

        Notification n1 = Notification.builder().user(user).type(NotificationType.NEW_COMMENT).message("Old").createdAt(LocalDateTime.now().minusHours(2)).build();
        Notification n2 = Notification.builder().user(user).type(NotificationType.NEW_COMMENT).message("New").createdAt(LocalDateTime.now()).build();
        Notification n3 = Notification.builder().user(user).type(NotificationType.NEW_COMMENT).message("Middle").createdAt(LocalDateTime.now().minusHours(1)).build();

        entityManager.persist(n1);
        entityManager.persist(n2);
        entityManager.persist(n3);
        entityManager.flush();

        List<Notification> results = notificationRepository.findByUser_UserIdOrderByCreatedAtDesc(user.getUserId());

        assertEquals(3, results.size());
        assertEquals("New", results.get(0).getMessage());
        assertEquals("Middle", results.get(1).getMessage());
        assertEquals("Old", results.get(2).getMessage());
    }

    @Test
    void TestTherapistQuery_SpecialtyIsolation() {
        Therapist t1 = Therapist.builder().fullName("Dr. Smith").email("smith@test.com").passwordHash("hash").specialisation("CBT").build();
        Therapist t2 = Therapist.builder().fullName("Dr. Jones").email("jones@test.com").passwordHash("hash").specialisation("PTSD").build();
        
        entityManager.persist(t1);
        entityManager.persist(t2);
        entityManager.flush();

        List<Therapist> cbtTherapists = therapistRepository.findBySpecialisation("CBT");
        assertEquals(1, cbtTherapists.size());
        assertEquals("Dr. Smith", cbtTherapists.get(0).getFullName());
    }

    @Test
    void TestUserDeletion_Cascade() {
        User user = User.builder().username("deleteMe").email("delete@test.com").passwordHash("hash").build();
        entityManager.persist(user);

        Story story = Story.builder().userId(user.getUserId()).categoryId(1L).title("Title").content("Content").build();
        // Set user to established mapped relation for cascade
        story.setUser(user);
        entityManager.persist(story);

        Comment comment = Comment.builder().userId(user.getUserId()).storyId(story.getStoryId()).content("Comm").build();
        comment.setUser(user);
        entityManager.persist(comment);

        entityManager.flush();
        entityManager.clear();

        User toDelete = userRepository.findById(user.getUserId()).orElseThrow();
        userRepository.delete(toDelete);
        entityManager.flush();

        // Assert stories and comments are gone
        assertTrue(storyRepository.findAll().isEmpty());
        assertTrue(commentRepository.findAll().isEmpty());
    }
}
