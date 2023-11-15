/**
 * <p>Описание:</p>
 * Сервисный класс для восстановления пароля пользователя. Обеспечивает процедуру смены пароля через отправку кода подтверждения на email пользователя.
 *
 * <p>Поля:</p>
 * <ul>
 *     <li>userRepository: Репозиторий пользователя.</li>
 *     <li>userService: Сервис, обрабатывающий общие операции с пользователями.</li>
 *     <li>passwordEncoder: Кодировщик для хеширования паролей.</li>
 *     <li>emailService: Сервис для отправки email сообщений.</li>
 * </ul>
 *
 * <p>Список методов:</p>
 * <ul>
 *     <li><b>preChange</b> - Предварительный этап смены пароля. Проверяет введенные данные, генерирует код подтверждения и отправляет его на email пользователя.</li>
 *     <li><b>recover</b> - Меняет пароль пользователя и сохраняет его в репозитории</li>
 * </ul>
 */

package ru.mtuci.MindScape.auth.service;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mtuci.MindScape.auth.dto.PassRecoverDto;
import ru.mtuci.MindScape.user.model.User;
import ru.mtuci.MindScape.user.repository.UserRepository;

@Service
@AllArgsConstructor
@Getter
public class PassRecoverService {

    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public void preChange(HttpSession session) {
        PassRecoverDto passRecoverDto = (PassRecoverDto) session.getAttribute("PassDTO");
        String email = passRecoverDto.getEmail();
        String pass = passRecoverDto.getPassword();
        String confirmPass = passRecoverDto.getConfirmPassword();
        userService.validateEmailAndPass(email, pass, confirmPass);
        userService.createAndSendCode(email, "recover");
    }

    @Transactional
    public void recover(HttpSession session) {
        PassRecoverDto passRecoverDto = (PassRecoverDto) session.getAttribute("PassDTO");
        String encodedPassword = passwordEncoder.encode(passRecoverDto.getPassword());
        User user = userRepository.findByEmail(passRecoverDto.getEmail()).get();
        user.setPassword(encodedPassword);

        userRepository.save(user);
        session.removeAttribute("PassDTO");
    }
}
