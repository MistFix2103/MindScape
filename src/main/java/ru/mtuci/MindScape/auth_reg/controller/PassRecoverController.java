package ru.mtuci.MindScape.auth_reg.controller;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.mtuci.MindScape.auth_reg.dto.PassRecoverDto;
import ru.mtuci.MindScape.auth_reg.dto.UserRegistrationDto;
import ru.mtuci.MindScape.auth_reg.service.PassRecoverService;
import ru.mtuci.MindScape.auth_reg.service.RegistrationService;

import javax.validation.Valid;
import static ru.mtuci.MindScape.exceptions.CustomExceptions.*;

@Controller
@RequestMapping("/forgot_password")
@AllArgsConstructor
public class PassRecoverController {
    private final PassRecoverService passService;
    private final RegistrationService registrationService;

    @PostMapping
    public String preRecover(@Valid @ModelAttribute PassRecoverDto passRecoverDto, Model model, HttpSession session) {
        try {
            passService.preChange(passRecoverDto);
            session.setAttribute("passRecoverDto", passRecoverDto);
            return "redirect:/forgot_password/verification";
        } catch (PasswordsDoNotMatchException | PasswordTooShortException | NewPassCanNotMatchOldPassException | EmailDoesNotExistException e) {
            model.addAttribute("error", e.getMessage());
            return "forgot_password";
        } catch (Exception e) {
            System.err.println("Неизвестная ошибка: " + e.getMessage());
            model.addAttribute("error", "Неизвестная ошибка!");
            return "forgot_password";
        }
    }

    @PostMapping("/verification")
    public String recover(@Valid @ModelAttribute PassRecoverDto passRecoverDto, Model model, HttpSession session, RedirectAttributes redirectAttributes){
        PassRecoverDto passDtoFromSession = (PassRecoverDto) session.getAttribute("passRecoverDto");

        if (passDtoFromSession == null) {
            model.addAttribute("error", "Произошла ошибка сессии. Попробуйте заново.");
            model.addAttribute("operationType", "recovery");
            return "verification";
        }

        passDtoFromSession.setCode(passRecoverDto.getCode());
        try {
            registrationService.validateCode(passDtoFromSession.getEmail(), passDtoFromSession.getCode());
            passService.recover(passDtoFromSession);
            redirectAttributes.addFlashAttribute("message", "Пароль успешно изменен!");
            return "redirect:/login";
        } catch (InvalidCodeException | CodeExpiredException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("operationType", "recovery");
            return "verification";
        } catch (Exception e) {
            System.err.println("Неизвестная ошибка: " + e.getMessage());
            model.addAttribute("error", "Неизвестная ошибка!");
            model.addAttribute("operationType", "recovery");
            return "verification";
        }
    }

    @GetMapping("/resendCode")
    public String resendCode(HttpSession session, Model model) {
        PassRecoverDto passRecoverDto = (PassRecoverDto) session.getAttribute("passRecoverDto");

        if (passRecoverDto == null || passRecoverDto.getEmail() == null) {
            model.addAttribute("error", "Не удалось повторно отправить код. Пожалуйста, попробуйте снова.");
            model.addAttribute("operationType", "recovery");
            return "verification";
        }
        try {
            registrationService.resendCode(passRecoverDto.getEmail(), 4);
        } catch (Exception e) {
            System.err.println("Неизвестная ошибка: " + e.getMessage());
            model.addAttribute("error", "Произошла ошибка при повторной отправке кода.");
        }
        model.addAttribute("operationType", "recovery");
        return "verification";
    }
}
