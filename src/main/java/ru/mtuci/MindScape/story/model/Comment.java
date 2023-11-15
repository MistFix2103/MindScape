package ru.mtuci.MindScape.story.model;

import jakarta.persistence.*;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Table(name = "comment")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    @NotBlank
    private String text;

    //@ManyToOne
    //@JoinColumn(name = "author_id")
    //private User author;

    //@ManyToOne
    //@JoinColumn(name = "story_id")
    //private Story story;

    private LocalDateTime time;
}