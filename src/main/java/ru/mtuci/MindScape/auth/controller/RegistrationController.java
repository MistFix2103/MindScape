package ru.mtuci.MindScape.auth.controller;

import jakarta.servlet.http.HttpSession;
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
@RequestMapping("/registration")
@AllArgsConstructor
public class RegistrationController {
    private final RegistrationService registrationService;

    @PostMapping("/user")
    public String preRegister(@Valid @ModelAttribute UserRegistrationDto registrationDto, Model model, HttpSession session) {
        try {
            registrationService.preRegister(registrationDto);
            session.setAttribute("registrationDto", registrationDto);
            return "redirect:/registration/verification";
        } catch (RuntimeException e) {
            String errorCode = e.getMessage();
            String errorMessage = errorMessage(errorCode);
            model.addAttribute("error", errorMessage);
            return "user";
        }
    }

    @PostMapping("/verification")
    public String register(@Valid @ModelAttribute UserRegistrationDto registrationDto, Model model, HttpSession session) {
        UserRegistrationDto registrationDtoFromSession = (UserRegistrationDto) session.getAttribute("registrationDto");
        registrationDtoFromSession.setCode(registrationDto.getCode());
        try {
            registrationService.validateCode(registrationDtoFromSession.getEmail(), registrationDtoFromSession.getCode());
            registrationService.register(registrationDtoFromSession);
            return "redirect:/login";
        } catch (RuntimeException e) {
            String errorCode = e.getMessage();
            String errorMessage = errorMessage(errorCode);
            model.addAttribute("error", errorMessage);
            return "verification";
        }
    }

    @GetMapping("/resendCode")
    public String resendCode(HttpSession session, Model model) {
        UserRegistrationDto registrationDto = (UserRegistrationDto) session.getAttribute("registrationDto");

        if (registrationDto == null || registrationDto.getEmail() == null) {
            model.addAttribute("error", "Не удалось повторно отправить код. Пожалуйста, попробуйте снова.");
            return "verification";
        }
        try {
            registrationService.resendCode(registrationDto.getEmail());
        } catch (Exception e) {
            model.addAttribute("error", "Произошла ошибка при повторной отправке кода.");
        }
        return "verification";
    }

    private String errorMessage(String errorCode) {
        return switch (errorCode) {
            case "EmailExists" -> "Пользователь с такой почтой уже существует!";
            case "PasswordTooShort" -> "Пароль слишком короткий!";
            case "PasswordsDoNotMatch" -> "Пароли не совпадают!";
            case "InvalidCode" -> "Неверный код!";
            case "CodeExpired" -> "Код устарел!";
            default -> "Неизвестная ошибка!";
        };
    }
}
