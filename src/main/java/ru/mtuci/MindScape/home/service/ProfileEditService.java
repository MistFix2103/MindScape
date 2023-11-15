/**
 * <p>Описание:</p>
 * Сервисный класс для изменения данных учетной записи.
 *
 * <p>Поля:</p>
 * <ul>
 *     <li>userRepository: Репозиторий пользователя.</li>
 *     <li>userService: Сервис, обрабатывающий общие операции с пользователями.</li>
 *     <li>passwordEncoder: Кодировщик для хеширования паролей.</li>
 * </ul>
 *
 * <p>Список методов:</p>
 * <ul>
 *     <li><b>changeName</b> - Меняет имя пользователя.</li>
 *     <li><b>preChangeMail</b> - Предварительный этап смены почты. Если новой почты нет в БД, на нее присылается код подтверждения.</li>
 *     <li><b>changeMail</b> - Меняет почту пользователя, сохраняет изменения в репозитории.</li>
 *     <li><b>preChangePass</b> - Предварительный этап смены пароля. Проверяет введенные данные, генерирует код подтверждения и отправляет его на email пользователя.</li>
 *     <li><b>changePass</b> - Меняет пароль пользователя и сохраняет его в репозитории.</li>
 * </ul>
 */

package ru.mtuci.MindScape.home.service;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mtuci.MindScape.auth.service.UserService;
import ru.mtuci.MindScape.user.model.User;
import ru.mtuci.MindScape.user.repository.UserRepository;

import static ru.mtuci.MindScape.exceptions.CustomExceptions.EmailExistsException;

@AllArgsConstructor
@Service
public class ProfileEditService {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Transactional
    public void changeName(String newName, String email, HttpSession session){
        User user = userRepository.findByEmail(email).get();
        user.setUsername(newName);
        userRepository.save(user);
        session.setAttribute("username", newName);
    }

    public void preChangeMail(String newMail) {
        if (userRepository.findByEmail(newMail).isPresent()) {
            throw new EmailExistsException();
        }
        userService.createAndSendCode(newMail, "mail_change");
    }

    @Transactional
    public void changeMail(String currentMail, String newMail){
        User user = userRepository.findByEmail(currentMail).get();
        user.setEmail(newMail);
        userRepository.save(user);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UsernamePasswordAuthenticationToken newAuth = new UsernamePasswordAuthenticationToken(
                newMail,
                authentication.getCredentials(),
                authentication.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(newAuth);
    }

    public void preChangePass(String newPass, String confirmPass, String email){
        userService.validateEmailAndPass(email, newPass, confirmPass);
        userService.createAndSendCode(email, "pass_change");
    }

    @Transactional
    public void changePass(String email, String newPass){
        User user =  userRepository.findByEmail(email).get();
        String encodedPassword = passwordEncoder.encode(newPass);
        user.setPassword(encodedPassword);

        userRepository.save(user);
    }
}
