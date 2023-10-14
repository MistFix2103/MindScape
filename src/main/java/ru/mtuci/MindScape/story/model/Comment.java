package ru.mtuci.MindScape.story.model;

import jakarta.persistence.*;
import lombok.Data;
import ru.mtuci.MindScape.user.model.User;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "comment")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String text;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    @ManyToOne
    @JoinColumn(name = "story_id")
    private Story story;

    private LocalDateTime time;
}