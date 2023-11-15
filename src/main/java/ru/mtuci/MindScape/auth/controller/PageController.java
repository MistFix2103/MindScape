/**
 * <p>Описание:</p>
 * Контроллер, обрабатывающий страницы приложения, связанные с входом в сервис, регистрацией и восстановлением пароля.
 */

package ru.mtuci.MindScape.auth.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
@AllArgsConstructor
public class PageController {
    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @GetMapping("/login/forgot_password")
    public String showPassRecoverPage() {
        return "forgot_password";
    }

    @GetMapping("/registration")
    public String showRegistrationPage() {
        return "registration";
    }

    @GetMapping("/registration/user")
    public String showUserRegistrationPage() {
        return "user";
    }

    @GetMapping("/registration/expert")
    public String showExpertRegistrationPage(Model model) {
        model.addAttribute("userType", "expert");
        return "expert";
    }

    @GetMapping("/registration/researcher")
    public String showResearcherRegistrationPage(Model model) {
        model.addAttribute("userType", "researcher");
        return "expert";
    }

    @GetMapping("/registration/verification")
    public String showVerificationRegistrationPage(Model model) {
        model.addAttribute("operationType", "registration");
        return "verification";
    }

    @GetMapping("/forgot_password/verification")
    public String showVerificationPassRecoverPage(Model model){
        model.addAttribute("operationType", "recovery");
        return "verification";
    }
}