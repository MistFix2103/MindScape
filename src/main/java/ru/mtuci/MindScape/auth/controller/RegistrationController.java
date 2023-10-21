package ru.mtuci.MindScape.auth.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.mtuci.MindScape.auth.dto.UserRegistrationDto;
import ru.mtuci.MindScape.auth.service.RegistrationService;

import javax.validation.Valid;

@Controller
@RequestMapping("/user_registration")
@AllArgsConstructor
public class RegistrationController {
    private final RegistrationService registrationService;

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute UserRegistrationDto registrationDto, Model model) {
        if (!registrationDto.getPassword().equals(registrationDto.getConfirmPassword())) {
            model.addAttribute("errorMessage", "Пароли не совпадают!");
            return "user_registration";
        }
        registrationService.register(registrationDto);
        return "redirect:/home";
    }
}
