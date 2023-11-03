/**
 * <p>Описание:</p>
 * Сервисный класс для восстановления пароля пользователя. Обеспечивает процедуру смены пароля через отправку кода подтверждения на email пользователя.
 *
 * <p>Поля:</p>
 * <ul>
 *     <li>userRepository, expertRepository, researcherRepository: Репозитории для различных типов пользователей.</li>
 *     <li>userService: Сервис, обрабатывающий общие операции с пользователями.</li>
 *     <li>userSession: Сервис для управления данными сессии пользователя во время работы веб-приложения.</li>
 *     <li>confirmationCodeRepository: Репозиторий для хранения кодов подтверждения.</li>
 *     <li>passwordEncoder: Кодировщик для хеширования паролей.</li>
 *     <li>emailService: Сервис для отправки email сообщений.</li>
 * </ul>
 *
 * <p>Список методов:</p>
 * <ul>
 *     <li>
 *         <b>preChange</b> - Предварительный этап смены пароля. Проверяет введенные данные, генерирует код подтверждения и отправляет его на email пользователя.
 *     </li>
 *     <li>
 *         <b>validate</b> - Проверяет введенные данные пользователя. Выбрасывает исключения, если данные не удовлетворяют критериям валидации.
 *     </li>
 *     <li>
 *         <b>recover</b> - Финальная фаза смены пароля. Проверяет код подтверждения, хеширует новый пароль и сохраняет его в соответствующем репозитории.
 *     </li>
 * </ul>
 */

package ru.mtuci.MindScape.auth_reg.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mtuci.MindScape.auth_reg.dto.PassRecoverDto;
import ru.mtuci.MindScape.auth_reg.session.UserSession;
import ru.mtuci.MindScape.user.model.*;
import ru.mtuci.MindScape.user.repository.ConfirmationCodeRepository;
import ru.mtuci.MindScape.user.repository.ExpertRepository;
import ru.mtuci.MindScape.user.repository.ResearcherRepository;
import ru.mtuci.MindScape.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static ru.mtuci.MindScape.auth_reg.service.RegistrationService.generateSixDigitCode;
import static ru.mtuci.MindScape.exceptions.CustomExceptions.*;

@Service
@AllArgsConstructor
@Getter
public class PassRecoverService {

    private final UserRepository userRepository;
    private final ExpertRepository expertRepository;
    private final ResearcherRepository researcherRepository;
    private final UserService userService;
    private final UserSession userSession;
    private final ConfirmationCodeRepository confirmationCodeRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public void preChange() {
        validate();
        String email = userSession.getPassRecoverDto().getEmail();

        confirmationCodeRepository.deleteByEmail(email);
        String confirmationCode = generateSixDigitCode();
        ConfirmationCode codeEntity = new ConfirmationCode();
        codeEntity.setEmail(email);
        codeEntity.setCode(confirmationCode);
        codeEntity.setExpirationDate(LocalDateTime.now().plusMinutes(10));
        codeEntity.setType(ConfirmationCode.Type.PASSWORD_RESET);
        confirmationCodeRepository.save(codeEntity);

        emailService.sendCodeEmail(email, confirmationCode, "recover");
    }

    public void validate() {
        PassRecoverDto passRecoverDto = userSession.getPassRecoverDto();
        Optional<?> user = userService.findUserByEmail(passRecoverDto.getEmail());
        if (user.isEmpty()) {
            throw new EmailDoesNotExistException();
        }

        if (passRecoverDto.getPassword().length() < 6) {
            throw new PasswordTooShortException();
        }

        if (!passRecoverDto.getPassword().equals(passRecoverDto.getConfirmPassword())) {
            throw new PasswordsDoNotMatchException();
        }

        if (passwordEncoder.matches(passRecoverDto.getPassword(), ((BaseUser) user.get()).getPassword())) {
            throw new NewPassCanNotMatchOldPassException();
        }
    }

    @Transactional
    public void recover() {
        PassRecoverDto passRecoverDto = userSession.getPassRecoverDto();
        ConfirmationCode storedCodeEntity = confirmationCodeRepository.findByEmail(passRecoverDto.getEmail());
        String encodedPassword = passwordEncoder.encode(passRecoverDto.getPassword());
        confirmationCodeRepository.delete(storedCodeEntity);

        Optional<?> user = userService.findUserByEmail(passRecoverDto.getEmail());
        BaseUser userEntity = (BaseUser) user.get();
        String role = String.valueOf(userEntity.getRole());
        userEntity.setPassword(encodedPassword);

        userService.saveUser(role, userEntity);
    }
}
