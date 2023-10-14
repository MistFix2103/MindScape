package ru.mtuci.MindScape.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.mtuci.MindScape.auth.service.AuthenticationService;

@RestController
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping
    public ResponseEntity<?> authenticateUser(@RequestParam String email, @RequestParam String password) {
        boolean isAuthenticated = authenticationService.authenticate(email, password);
        if (isAuthenticated) {
            return ResponseEntity.ok().body("Успешный вход!");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Ошибка аутентификации!");
        }
    }
}
