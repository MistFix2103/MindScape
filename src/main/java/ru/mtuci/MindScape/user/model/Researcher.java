package ru.mtuci.MindScape.user.model;

import jakarta.persistence.*;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.Set;
import java.util.UUID;

@Entity
@Data
@Table(name = "researcher")
public class Researcher {
    @Id
    @GeneratedValue
    private UUID id;

    @NotBlank
    private String name;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String password;

    private String researchField;

    @ElementCollection
    private Set<String> certificates;

    @ElementCollection
    private Set<String> publications;
}