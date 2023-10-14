package ru.mtuci.MindScape.auth.dto;

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

        @NotEmpty()
        private String confirmPassword;
}