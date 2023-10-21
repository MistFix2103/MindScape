package ru.mtuci.MindScape.auth.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.mtuci.MindScape.auth.dto.UserRegistrationDto;
import ru.mtuci.MindScape.auth.service.RegistrationService;

import javax.validation.Valid;

@Controller
@RequestMapping("/registration/user")
@AllArgsConstructor
public class RegistrationController {
    private final RegistrationService registrationService;

    @PostMapping
    public String register(@Valid @ModelAttribute UserRegistrationDto registrationDto, Model model) {
        try {
            registrationService.register(registrationDto);
            return "redirect:/login";
        } catch (RuntimeException e) {
            String errorCode = e.getMessage();
            String errorMessage = errorMessage(errorCode);
            model.addAttribute("error", errorMessage);
            return "user";
        }
    }

    private String errorMessage(String errorCode) {
        return switch (errorCode) {
            case "EmailExists" -> "Пользователь с такой почтой уже существует!";
            case "PasswordTooShort" -> "Пароль слишком короткий!";
            case "PasswordsDoNotMatch" -> "Пароли не совпадают!";
            default -> "Неизвестная ошибка";
        };
    }
}
