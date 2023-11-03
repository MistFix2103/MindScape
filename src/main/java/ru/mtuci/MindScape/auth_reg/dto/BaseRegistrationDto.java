/**
 * <p>Описание:</p>
 * Базовый Data Transfer Object (DTO) для операций, связанных с регистрацией.
 * Этот класс предоставляет основные поля и аннотации валидации для объектов регистрации.
 * Наследуется в специализированных DTO для различных типов пользователей (user, expert, researcher).
 */

package ru.mtuci.MindScape.auth_reg.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseRegistrationDto {
    @NotEmpty
    private String name;

    @NotEmpty
    @Email
    private String email;

    @NotEmpty
    @Size(min = 6)
    private String password;

    @NotEmpty
    private String confirmPassword;

    private String code;
}