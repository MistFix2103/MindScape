/**
 * <p>Описание:</p>
 * Сервис, обрабатывающий вход в систему.
 */

package ru.mtuci.MindScape.auth.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Service;
import ru.mtuci.MindScape.user.model.User;
import ru.mtuci.MindScape.user.repository.UserRepository;

import java.io.IOException;
import java.util.Optional;

@Service
@AllArgsConstructor
public class LoginService {
    private final UserRepository userRepository;

    public void authorizeUser(HttpServletResponse response, Authentication authentication, HttpServletRequest request) throws IOException {
        Optional<User> user = userRepository.findByEmail(authentication.getName());
        if (user.get().isTwo_step()) {
            response.sendRedirect("/login/2fa");
        } else {
            CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
            if (csrfToken != null) {
                Cookie cookie = new Cookie("XSRF-TOKEN", csrfToken.getToken());
                cookie.setPath("/");
                response.addCookie(cookie);
            }
            response.sendRedirect("/home");
        }
    }
}
