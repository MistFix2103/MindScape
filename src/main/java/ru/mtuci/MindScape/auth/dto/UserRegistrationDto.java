/**
 * <p>Описание:</p>
 * Класс DTO, используемый для регистрации пользователя.
 * Содержит данные, необходимые для создания нового пользователя, включая имя, почту, пароль и подтверждение пароля,
 * документ (в случае экспертов и исследователей) и роль пользователя.
 */

package ru.mtuci.MindScape.auth.dto;

import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationDto {
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

    @Lob
    private byte[] document;

    private String role;
}
