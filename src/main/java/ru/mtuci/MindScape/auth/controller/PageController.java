package ru.mtuci.MindScape.auth.controller;

import jakarta.servlet.http.HttpSession;
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

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        return "redirect:/login?logout=true";
    }
}
