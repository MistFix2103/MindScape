/**
 * <p>Описание</p>
 * Абстрактный базовый класс для пользователей, содержит общие поля и аннотации для работы с базой данных.
 *
 * <p>Поля:</p>
 * <ul>
 *     <li>id: Уникальный идентификатор.</li>
 *     <li>username: Имя пользователя.</li>
 *     <li>email: Электронная почта.</li>
 *     <li>password: Пароль.</li>
 *     <li>role: Роль пользователя.</li>
 * </ul>
 */

package ru.mtuci.MindScape.user.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.UUID;

@MappedSuperclass
@Data
@EqualsAndHashCode(callSuper = false)
public abstract class BaseUser {
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
}