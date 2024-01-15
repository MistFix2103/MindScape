package ru.mtuci.MindScape.story.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mtuci.MindScape.story.model.Story;
import ru.mtuci.MindScape.story.model.StoryStatus;

import java.util.List;
import java.util.UUID;

public interface StoryRepository extends JpaRepository<Story, UUID> {
    List<Story> findByAuthor_IdAndStatus(UUID authorId, StoryStatus status);
    List<Story> findAllByStatusOrderByTimeDesc(StoryStatus status);
}
