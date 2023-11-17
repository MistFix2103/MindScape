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
 *     <li><b>validateName</b> - Проверяет на корректность имя пользователя.</li>
 *     <li><b>changeName</b> - Меняет имя пользователя.</li>
 *     <li><b>preChangeMail</b> - Предварительный этап смены почты. Если новой почты нет в БД, на нее присылается код подтверждения.</li>
 *     <li><b>changeMail</b> - Меняет почту пользователя, сохраняет изменения в репозитории.</li>
 *     <li><b>manage2FA</b> - Подключает/отключает двухфакторную аутентификацию.</li>
 *     <li><b>changeImage</b> - Меняет изображение профиля.</li>
 *     <li><b>deleteImage</b> - Удаляет изображение профиля.</li>
 *     <li><b>preChangePass</b> - Предварительный этап смены пароля. Проверяет введенные данные, генерирует код подтверждения и отправляет его на email пользователя.</li>
 *     <li><b>changePass</b> - Меняет пароль пользователя и сохраняет его в репозитории.</li>
 * </ul>
 */

package ru.mtuci.MindScape.home.service;

import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.mtuci.MindScape.auth.service.UserService;
import ru.mtuci.MindScape.user.model.User;
import ru.mtuci.MindScape.user.repository.UserRepository;

import java.io.IOException;

import static ru.mtuci.MindScape.exceptions.CustomExceptions.*;

@AllArgsConstructor
@Service
public class ProfileEditService {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public void validateName(String newName){
        if (newName.length() > 24) {
            throw new NameIsTooLongException();
        }

        if (!newName.matches("^[\\p{L}\\s]+$")) {
            throw new IncorrectNameException();
        }
    }

    @Transactional
    public void changeName(String newName, String email){
        User user = userRepository.findByEmail(email).get();
        user.setUsername(newName);
        userRepository.save(user);
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

    @Transactional
    public void manage2FA(String email) {
        User user = userRepository.findByEmail(email).get();
        user.setTwo_step(!user.isTwo_step());
        userRepository.save(user);
    }

    @Transactional
    public void changeImage(MultipartFile image, String email) throws IOException {
        User user = userRepository.findByEmail(email).get();
        byte[] imageBytes = image.getBytes();
        user.setImage(imageBytes);
        userRepository.save(user);
    }

    @Transactional
    public void deleteImage(String email) {
        User user = userRepository.findByEmail(email).get();
        user.setImage(null);
        userRepository.save(user);
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
