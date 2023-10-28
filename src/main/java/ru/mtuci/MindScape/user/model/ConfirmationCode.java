package ru.mtuci.MindScape.user.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
public class ConfirmationCode {
    public enum Type {
        USER_REGISTRATION,
        EXPERT_REGISTRATION,
        RESEARCHER_REGISTRATION,
        PASSWORD_RESET
    }

    @Id
    @GeneratedValue
    private UUID id;
    private String code;
    private LocalDateTime expirationDate;
    private String email;
    private boolean confirmed;

    @OneToOne
    private User user;

    @Enumerated(EnumType.STRING)
    private Type type;
}
