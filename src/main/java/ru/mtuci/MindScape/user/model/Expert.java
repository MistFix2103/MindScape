package ru.mtuci.MindScape.user.model;

import jakarta.persistence.*;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.Set;
import java.util.UUID;

@Entity
@Data
@Table(name = "expert")
public class Expert {
    @Id
    @GeneratedValue
    private UUID id;;

    @NotBlank
    private String name;

    @Email
    @NotBlank
    @Column(unique = true)
    private String email;

    @NotBlank
    private String password;

    @ElementCollection
    private Set<String> certificates;
}