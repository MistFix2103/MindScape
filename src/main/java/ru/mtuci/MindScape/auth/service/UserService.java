/**
 * <p>Описание:</p>
 * Сервисный класс для методов, которые используются в разных контроллерах/сервисах. Включает в себя методы для создания и отправки кода подтверждения и проверки данных.
 *
 * <p>Список методов:</p>
 * <ul>
 *     <li><b>createAndSendCode</b> - Создает и отправляет письмо с кодом подтверждения на указанную почту. Код действителен 10 минут.</li>
 *     <li><b>generateSixDigitCode</b> - Генерирует 6-значный код.</li>
 *     <li><b>validateCode</b> - Проверяет корректность и актуальность кода, который ввел пользователь. Если все проверки прошли, код удаляется.</li>
 *     <li><b>validateEmailAndPass</b> - Проверяет корректность введенной почты и пароля.</li>
 *     <li><b>checkCaptcha</b> - Проверяет, верный ли ответ на капчу был дан пользователем.</li>
 *     <li><b>generateCaptcha</b> - Генерирует 2 числа для капчи</li>
 * </ul>
 */

package ru.mtuci.MindScape.auth.service;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.mtuci.MindScape.user.model.ConfirmationCode;
import ru.mtuci.MindScape.user.model.User;
import ru.mtuci.MindScape.user.repository.ConfirmationCodeRepository;
import ru.mtuci.MindScape.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static ru.mtuci.MindScape.exceptions.CustomExceptions.*;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final ConfirmationCodeRepository confirmationCodeRepository;
    private final PasswordEncoder passwordEncoder;

    public void createAndSendCode(String mail, String type) {
        confirmationCodeRepository.deleteByEmail(mail);
        String confirmationCode = generateSixDigitCode();
        ConfirmationCode codeEntity = new ConfirmationCode();
        codeEntity.setEmail(mail);
        codeEntity.setCode(confirmationCode);
        codeEntity.setExpirationDate(LocalDateTime.now().plusMinutes(10));

        String code_type = switch (type) {
            case "mail_change" -> "CHANGE_EMAIL";
            case "pass_change" -> "CHANGE_PASSWORD";
            case "recover" -> "PASSWORD_RESET";
            case "user" -> "USER_REGISTRATION";
            case "expert" -> "EXPERT_REGISTRATION";
            case "two-step" -> "TWO_FACTOR";
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };
        codeEntity.setType(ConfirmationCode.Type.valueOf(code_type));
        confirmationCodeRepository.save(codeEntity);
        emailService.sendCodeEmail(mail, confirmationCode, type);
    }

    private static String generateSixDigitCode() {
        Random random = new Random();
        int code = random.nextInt(900000) + 100000;
        return String.valueOf(code);
    }

    public void validateCode(String email, String code) {
        ConfirmationCode storedCodeEntity = confirmationCodeRepository.findByEmail(email);
        if (!storedCodeEntity.getCode().equals(code)) {
            throw new InvalidCodeException();
        }

        LocalDateTime now = LocalDateTime.now();
        if (storedCodeEntity.getExpirationDate().isBefore(now)) {
            throw new CodeExpiredException();
        }
        confirmationCodeRepository.deleteByEmail(email);
    }

    public void validateEmailAndPass(String email, String pass, String confirmPass) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw new EmailDoesNotExistException();
        }
        if (pass.length() < 6) {
            throw new PasswordTooShortException();
        }
        if (!pass.equals(confirmPass)) {
            throw new PasswordsDoNotMatchException();
        }
        if (passwordEncoder.matches(pass, user.get().getPassword())) {
            throw new NewPassCanNotMatchOldPassException();
        }
    }

    public void checkCaptcha(int num1, int num2, int answer, String role, String email) {
        if ((num1 + num2) == answer) {
            createAndSendCode(email, role);
        } else {
            throw new IncorrectCaptchaCodeException();
        }
    }

    public Map<String, Integer> generateCaptcha(HttpSession session) {
        int num1 = ThreadLocalRandom.current().nextInt(10, 50);
        int num2 = ThreadLocalRandom.current().nextInt(10, 50);
        session.setAttribute("num1", num1);
        session.setAttribute("num2", num2);
        return Map.of("num1", num1, "num2", num2);
    }
}