package ru.mtuci.MindScape.auth_reg.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.mtuci.MindScape.user.model.User;
import ru.mtuci.MindScape.user.repository.UserRepository;

import java.util.Optional;

@Controller
@RequestMapping("/")
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
    public String showExpertRegistrationPage() {
        return "expert";
    }

    @GetMapping("/registration/researcher")
    public String showResearcherRegistrationPage() {
        return "researcher";
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

    @Autowired
    private UserRepository userRepository;
    @GetMapping("/home")
    public String home(Authentication authentication, Model model) {
        String email = authentication.getName();
        Optional<User> user = userRepository.findByEmail(email);
        String username = user.get().getUsername();
        model.addAttribute("username", username);
        return "home";
    }
}