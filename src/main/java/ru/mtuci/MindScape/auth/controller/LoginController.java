/**
 * <p>Описание:</p>
 * Контроллер, обрабатывающий вход в систему.
 */

package ru.mtuci.MindScape.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.mtuci.MindScape.auth.service.UserService;

@Controller
@RequestMapping("/login")
@AllArgsConstructor
public class LoginController {
    private final UserService userService;

    @GetMapping("/2fa")
    public String showVerificationPage(Authentication authentication, Model model) {
        userService.createAndSendCode(authentication.getName(), "two-step");
        model.addAttribute("operationType", "2fa");
        return "verification";
    }

    @PostMapping("/2fa/verify")
    public String verify(@RequestParam String code, Authentication authentication, HttpServletRequest request) {
        request.setAttribute("operationType", "2fa");
        userService.validateCode(authentication.getName(), code);
        return "redirect:/home";
    }
}
