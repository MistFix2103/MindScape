package ru.mtuci.MindScape.auth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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

    @GetMapping("/user_registration")
    public String showUserRegistrationPage() {
        return "user_registration";
    }

    @GetMapping("/home")
    public String home() {
        return "home";
    }
}
