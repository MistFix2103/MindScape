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

@Controller
@RequestMapping("/home")
@AllArgsConstructor
public class HomePageController {
    private final UserRepository userRepository;

    @GetMapping
    public String home(Authentication authentication, Model model, HttpSession session) {
        User user = userRepository.findByEmail(authentication.getName()).get();
        String username = user.getUsername();
        if (session.getAttribute("username") == null) {
            session.setAttribute("username", username);
        }
        model.addAttribute("user_id", user.getId());
        model.addAttribute("username", username);
        return "home";
    }
    @GetMapping("/profile")
    public String showProfilePage(Model model, Authentication authentication, HttpSession session) {
        User user = userRepository.findByEmail(authentication.getName()).get();
        String username = (String) session.getAttribute("username");
        String role = String.valueOf(user.getRole());
        model.addAttribute("username", username);
        model.addAttribute("user", user);

        boolean flag = !(role.equals("USER"));
        model.addAttribute("showStatus", flag);
        if (flag) {
            model.addAttribute("isVerified", user.isVerified());
        }
        return "profile";
    }

    @GetMapping("/profile/verification")
    public String showVerificationPage(){
        return "verification";
    }
}
