package ru.mtuci.MindScape.story.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Table(name = "story")
public class Story {
    @Id
    @GeneratedValue
    private UUID id;

    @NotBlank
    private String text;

    //@ManyToOne
    //@JoinColumn(name = "author_id")
    //private User author;

    //@OneToMany(mappedBy = "story")
    //private Set<Comment> comments;

    private LocalDateTime time;
}
