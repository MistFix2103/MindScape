package ru.mtuci.MindScape.story.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Data
@Table(name = "story_usage_req")
public class StoryUsageReq {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    //@ManyToOne
    //@JoinColumn(name = "story_id")
    //private Story story;

    //@ManyToOne
    //@JoinColumn(name = "researcher_id")
    //private Researcher researcher;
}