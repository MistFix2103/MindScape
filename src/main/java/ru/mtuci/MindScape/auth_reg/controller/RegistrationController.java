package ru.mtuci.MindScape.auth_reg.controller;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.mtuci.MindScape.auth_reg.dto.UserRegistrationDto;
import ru.mtuci.MindScape.auth_reg.service.RegistrationService;

import javax.validation.Valid;
import static ru.mtuci.MindScape.exceptions.CustomExceptions.*;

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
        } catch (EmailExistsException | PasswordTooShortException | PasswordsDoNotMatchException e) {
            model.addAttribute("error", e.getMessage());
            return "user";
        } catch (Exception e) {
            System.err.println("Неизвестная ошибка: " + e.getMessage());
            model.addAttribute("error", "Неизвестная ошибка!");
            return "user";
        }
    }

    @PostMapping("/verification")
    public String register(@Valid @ModelAttribute UserRegistrationDto registrationDto, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        UserRegistrationDto registrationDtoFromSession = (UserRegistrationDto) session.getAttribute("registrationDto");

        if (registrationDtoFromSession == null) {
            model.addAttribute("error", "Произошла ошибка сессии. Попробуйте заново.");
            model.addAttribute("operationType", "registration");
            return "verification";
        }

        registrationDtoFromSession.setCode(registrationDto.getCode());
        try {
            registrationService.validateCode(registrationDtoFromSession.getEmail(), registrationDtoFromSession.getCode());
            registrationService.register(registrationDtoFromSession);
            redirectAttributes.addFlashAttribute("message", "Вы успешно зарегистрировались!");
            return "redirect:/login";
        } catch (InvalidCodeException | CodeExpiredException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("operationType", "registration");
            return "verification";
        }
    }

    @GetMapping("/resendCode")
    public String resendCode(HttpSession session, Model model) {
        UserRegistrationDto registrationDto = (UserRegistrationDto) session.getAttribute("registrationDto");

        if (registrationDto == null || registrationDto.getEmail() == null) {
            model.addAttribute("error", "Не удалось повторно отправить код. Пожалуйста, попробуйте снова.");
            model.addAttribute("operationType", "registration");
            return "verification";
        }

        try {
            registrationService.resendCode(registrationDto.getEmail(), 1);
        } catch (Exception e) {
            model.addAttribute("error", "Произошла ошибка при повторной отправке кода.");
        }
        model.addAttribute("operationType", "registration");
        return "verification";
    }
}
