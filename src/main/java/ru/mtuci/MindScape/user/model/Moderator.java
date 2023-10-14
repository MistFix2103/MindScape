package ru.mtuci.MindScape.user.model;

import jakarta.persistence.*;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.UUID;

@Entity
@Data
@Table(name = "moderator")
public class Moderator {
    @Id
    @GeneratedValue
    private UUID id;

    @NotBlank
    private String name;

    @NotBlank
    private String password;
}