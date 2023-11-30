/**
 * <p>Описание:</p>
 * Контроллер, обрабатывающий страницы приложения, связанные с использованием сервиса.
 */

package ru.mtuci.MindScape.home.controller;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.mtuci.MindScape.user.model.User;
import ru.mtuci.MindScape.user.repository.UserRepository;

import java.util.Base64;
import java.util.Optional;

@Controller
@RequestMapping("/home")
@AllArgsConstructor
public class HomePageController {
    private final UserRepository userRepository;

    @GetMapping
    public String home(Authentication authentication, Model model) {
        User user = userRepository.findByEmail(authentication.getName()).get();
        String username = user.getUsername();
        Optional.ofNullable(user.getImage())
                .map(Base64.getEncoder()::encodeToString)
                .ifPresent(img -> model.addAttribute("userImage", "data:image/png;base64," + img));
        model.addAttribute("username", username);
        return "home";
    }

    @GetMapping("/profile")
    public String showProfilePage(Model model, Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName()).get();
        model.addAttribute("username", user.getUsername());
        model.addAttribute("user", user);
        model.addAttribute("showStatus", !String.valueOf(user.getRole()).equals("USER"));

        Optional.ofNullable(user.getImage())
                .map(Base64.getEncoder()::encodeToString)
                .ifPresent(img -> model.addAttribute("userImage", "data:image/png;base64," + img));

        return "profile";
    }

    @GetMapping("/profile/verification")
    public String showVerificationPage(HttpSession session, Model model){
        Optional<String> newMail = Optional.ofNullable((String) session.getAttribute("newMail"));
        Optional<String> newPass = Optional.ofNullable((String) session.getAttribute("newPass"));
        newMail.ifPresent(mail -> model.addAttribute("operationType", "mail_change"));
        newPass.ifPresent(pass -> model.addAttribute("operationType", "password_change"));
        return "verification";
    }
}
