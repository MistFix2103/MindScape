package ru.mtuci.MindScape.home.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.mtuci.MindScape.story.model.Story;
import ru.mtuci.MindScape.story.model.StoryStatus;
import ru.mtuci.MindScape.story.repository.StoryRepository;
import ru.mtuci.MindScape.user.model.User;
import ru.mtuci.MindScape.user.repository.UserRepository;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StoryServiceTest {

    @Mock
    private StoryRepository storyRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private StoryService storyService;

    @Test
    void saveStory_PublishTrue_StorySavedAsPublished() {
        // Arrange
        String text = "Test story text";
        String author = "test@example.com";
        boolean publish = true;

        // Создаем заглушку (mock) для UserRepository
        User user = new User();
        when(userRepository.findByEmail(author)).thenReturn(Optional.of(user));

        // Создаем заглушку (mock) для StoryRepository
        when(storyRepository.save(any(Story.class))).thenReturn(new Story()); // Используйте реальный объект Story или уточните нужные вам параметры

        // Act
        storyService.saveStory(text, author, publish);

        // Assert
        verify(storyRepository, times(1)).save(any(Story.class));
    }


    @Test
    void editStory_StoryEditedSuccessfully() {
        // Arrange
        String newText = "Updated story text";
        UUID storyId = UUID.randomUUID();
        Story existingStory = new Story();
        existingStory.setId(storyId);
        existingStory.setText("Old story text");

        when(storyRepository.findById(storyId)).thenReturn(Optional.of(existingStory));
        when(storyRepository.save(any(Story.class))).thenReturn(null);

        // Act
        storyService.editStory(newText, storyId);

        // Assert
        verify(storyRepository, times(1)).save(any(Story.class));
    }

    @Test
    void publishDraft_StoryPublishedSuccessfully() {
        // Arrange
        String newText = "Published story text";
        UUID storyId = UUID.randomUUID();
        Story existingDraft = new Story();
        existingDraft.setId(storyId);
        existingDraft.setText("Draft story text");
        existingDraft.setStatus(StoryStatus.DRAFT);

        when(storyRepository.findById(storyId)).thenReturn(Optional.of(existingDraft));
        when(storyRepository.save(any(Story.class))).thenReturn(null);

        // Act
        storyService.publishDraft(newText, storyId);

        // Assert
        verify(storyRepository, times(1)).save(any(Story.class));
    }
}
