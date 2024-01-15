package ru.mtuci.MindScape.home.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mtuci.MindScape.story.model.Story;
import ru.mtuci.MindScape.story.model.StoryStatus;
import ru.mtuci.MindScape.story.repository.StoryRepository;
import ru.mtuci.MindScape.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class StoryService {
    private final StoryRepository storyRepository;
    private final UserRepository userRepository;

    @Transactional
    public void saveStory(String text, String author, boolean publish) {
        Story story = new Story();
        story.setTime(LocalDateTime.now());
        story.setText(text);
        story.setAuthor(userRepository.findByEmail(author).get());
        story.setStatus(publish ? StoryStatus.PUBLISHED : StoryStatus.DRAFT);
        storyRepository.save(story);
    }

    @Transactional
    public void editStory(String text, UUID id) {
        Story story = storyRepository.findById(id).get();
        story.setText(text);
        storyRepository.save(story);
    }

    @Transactional
    public void publishDraft(String text, UUID id) {
        Story story = storyRepository.findById(id).get();
        story.setText(text);
        story.setStatus(StoryStatus.PUBLISHED);
        storyRepository.save(story);
    }
}
