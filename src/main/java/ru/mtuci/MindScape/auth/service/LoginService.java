/**
 * <p>Описание:</p>
 * Сервис, обрабатывающий вход в систему.
 */

package ru.mtuci.MindScape.auth.service;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ru.mtuci.MindScape.user.model.User;
import ru.mtuci.MindScape.user.repository.UserRepository;

import java.io.IOException;
import java.util.Optional;

@Service
@AllArgsConstructor
public class LoginService {
    private final UserRepository userRepository;

    public void authorizeUser(HttpServletResponse response, Authentication authentication) throws IOException {
        Optional<User> user = userRepository.findByEmail(authentication.getName());
        response.sendRedirect(user.get().isTwo_step() ? "/login/2fa" : "/home");
    }
}
