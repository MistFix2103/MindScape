package ru.mtuci.MindScape.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "story_usage_req")
public class StoryUsageReq {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "story_id")
    private Story story;

    @ManyToOne
    @JoinColumn(name = "researcher_id")
    private Researcher researcher;
}