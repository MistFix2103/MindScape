package ru.mtuci.MindScape.user.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import ru.mtuci.MindScape.story.model.Story;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    @Email
    @NotBlank
    private String email;

    @Column(nullable = false)
    @NotBlank
    private String password;

    @OneToMany(mappedBy = "author")
    private Set<Story> stories = new HashSet<>();

    @Enumerated(value = EnumType.STRING)
    @Column(name ="role")
    private UserRole role;
}