/**
 * <p>Описание:</p>
 * Модуль для регистрации новых пользователей. Обеспечивает процедуру регистрации через отправку кода подтверждения на email.
 *
 * <p>Поля:</p>
 * <ul>
 *     <li>userRepository: Репозиторий для пользователя.</li>
 *     <li>userService: Сервис, обрабатывающий общие операции с пользователями.</li>
 *     <li>passwordEncoder: Кодировщик для хеширования паролей.</li>
 *     <li>emailService: Сервис для отправки email сообщений.</li>
 * </ul>
 *
 * <p>Список методов:</p>
 * <ul>
 *     <li><b>preRegister</b> - Проверяет введенные данные, генерирует код подтверждения и отправляет его на email пользователя.</li>
 *     <li><b>register</b> - Финальный этап регистрации, создание новой учетной записи.</li>
 * </ul>
 */

package ru.mtuci.MindScape.auth.service;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mtuci.MindScape.auth.dto.UserRegistrationDto;
import ru.mtuci.MindScape.user.model.User;
import ru.mtuci.MindScape.user.model.UserRole;
import ru.mtuci.MindScape.user.repository.UserRepository;

import static ru.mtuci.MindScape.exceptions.CustomExceptions.*;

@Service
@AllArgsConstructor
public class RegistrationService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public void preRegister(HttpSession session) {
        UserRegistrationDto regDto = (UserRegistrationDto) session.getAttribute("DTO");
        String email = regDto.getEmail();
        String username = regDto.getName();
        if (username.length() > 24) {
            throw new NameIsTooLongException();
        }

        if (!username.matches("^[\\p{L}\\s]+$")) {
            throw new IncorrectNameException();
        }

        if (userRepository.findByEmail(email).isPresent()) {
            throw new EmailExistsException();
        }

        if (regDto.getPassword().length() < 6) {
            throw new PasswordTooShortException();
        }

        if (!regDto.getPassword().equals(regDto.getConfirmPassword())) {
            throw new PasswordsDoNotMatchException();
        }
    }

    @Transactional
    public void register(HttpSession session) {
        UserRegistrationDto regDto = (UserRegistrationDto) session.getAttribute("DTO");
        User user = new User();
        String role = regDto.getRole();
        user.setUsername(regDto.getName());
        user.setEmail(regDto.getEmail());
        user.setPassword(passwordEncoder.encode(regDto.getPassword()));
        user.setRole(UserRole.valueOf(role.toUpperCase()));
        user.setImageBase64("/images/user_logo.png");
        if (!role.equals("user")) {
            user.setDocument(regDto.getDocument());
            user.setVerified(false);
        }
        userRepository.save(user);
        session.removeAttribute("DTO");
    }
}
