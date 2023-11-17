/**
 * <p>Описание:</p>
 * Класс для представления пользователя.
 */

package ru.mtuci.MindScape.user.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.UUID;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class User {
    @Id
    @GeneratedValue
    private UUID id;

    @NotBlank
    private String username;

    @Email
    @NotBlank
    @Column(unique = true)
    private String email;

    @NotBlank
    private String password;

    @Enumerated(value = EnumType.STRING)
    @Column(name ="role")
    private UserRole role;

    @Column(name = "2FA", nullable = false, columnDefinition = "TINYINT(1) default '0'")
    private boolean two_step;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] document;

    @Column(columnDefinition = "TINYINT(1)")
    private boolean verified;

    @Lob
    @Column(columnDefinition = "MEDIUMBLOB ")
    private byte[] image;
}
