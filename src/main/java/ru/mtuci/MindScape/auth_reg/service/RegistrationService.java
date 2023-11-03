/**
 * <p>Описание:</p>
 * Сервисный класс для регистрации новых пользователей. Обеспечивает процедуру регистрации через отправку кода подтверждения на email.
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
 *         <b>preRegister</b> - Предварительная фаза регистрации. Проверяет введенные данные, генерирует код подтверждения и отправляет его на email пользователя.
 *     </li>
 *     <li>
 *         <b>register</b> - Финальная фаза регистрации. Создает новую учетную запись пользователя с учетом его роли и сохраняет ее в соответствующем репозитории.
 *     </li>
 *     <li>
 *         <b>validateRegistration</b> - Приватный метод для валидации данных, введенных при регистрации.
 *     </li>
 *     <li>
 *         <b>validateCode</b> - Валидация кода подтверждения, отправленного на email пользователя.
 *     </li>
 *     <li>
 *         <b>resendCode</b> - Переотправка кода подтверждения на email пользователя.
 *     </li>
 *     <li>
 *         <b>generateSixDigitCode</b> - Статический метод для генерации шестизначного кода подтверждения.
 *     </li>
 * </ul>
 */

package ru.mtuci.MindScape.auth_reg.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mtuci.MindScape.auth_reg.dto.BaseRegistrationDto;
import ru.mtuci.MindScape.auth_reg.dto.ExpertRegistrationDto;
import ru.mtuci.MindScape.auth_reg.session.UserSession;
import ru.mtuci.MindScape.user.model.*;
import ru.mtuci.MindScape.user.repository.ConfirmationCodeRepository;
import ru.mtuci.MindScape.user.repository.ExpertRepository;
import ru.mtuci.MindScape.user.repository.ResearcherRepository;
import ru.mtuci.MindScape.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Random;

import static ru.mtuci.MindScape.exceptions.CustomExceptions.*;

@Service
@AllArgsConstructor
@Getter
public class RegistrationService {

    private final UserRepository userRepository;
    private final ExpertRepository expertRepository;
    private final ResearcherRepository researcherRepository;
    private final UserService userService;
    private final UserSession userSession;
    private final ConfirmationCodeRepository confirmationCodeRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public void preRegister() {
        validateRegistration();
        String email = userSession.getRegistrationDto().getEmail();

        confirmationCodeRepository.deleteByEmail(email);
        String confirmationCode = generateSixDigitCode();
        ConfirmationCode codeEntity = new ConfirmationCode();
        codeEntity.setEmail(email);
        codeEntity.setCode(confirmationCode);
        codeEntity.setExpirationDate(LocalDateTime.now().plusMinutes(10));

        String role = userSession.getRole().equals("user") ? "user" : "expert";
        codeEntity.setType(ConfirmationCode.Type.valueOf(role.toUpperCase() + "_REGISTRATION"));
        emailService.sendCodeEmail(email, confirmationCode, role);
        confirmationCodeRepository.save(codeEntity);
    }

    @Transactional
    public void register() {
        BaseRegistrationDto registrationDto = userSession.getRegistrationDto();
        String role = userSession.getRole();
        ConfirmationCode storedCodeEntity = confirmationCodeRepository.findByEmail(registrationDto.getEmail());
        BaseUser entity = userService.createUser(role);

        entity.setUsername(registrationDto.getName());
        entity.setEmail(registrationDto.getEmail());
        entity.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        entity.setRole(UserRole.valueOf(role.toUpperCase()));

        if (registrationDto instanceof ExpertRegistrationDto) {
            byte[] document = ((ExpertRegistrationDto) registrationDto).getDocument();
            if (entity instanceof Expert) {
                ((Expert) entity).setDocument(document);
            } else {
                ((Researcher) entity).setDocument(document);
            }
        }
        userService.saveUser(role, entity);
        confirmationCodeRepository.delete(storedCodeEntity);
    }

    private void validateRegistration() {
        BaseRegistrationDto registrationDto = userSession.getRegistrationDto();
        if (userService.findUserByEmail(registrationDto.getEmail()).isPresent()) {
            throw new EmailExistsException();
        }

        if (registrationDto.getPassword().length() < 6) {
            throw new PasswordTooShortException();
        }

        if (!registrationDto.getPassword().equals(registrationDto.getConfirmPassword())) {
            throw new PasswordsDoNotMatchException();
        }
    }

    public void validateCode(String email, String code) {
        ConfirmationCode storedCodeEntity = confirmationCodeRepository.findByEmail(email);

        LocalDateTime now = LocalDateTime.now();
        if (storedCodeEntity.getExpirationDate().isBefore(now)) {
            throw new CodeExpiredException();
        }

        if (!storedCodeEntity.getCode().equals(code)) {
            throw new InvalidCodeException();
        }
    }

    public void resendCode(String email, String role) {
        confirmationCodeRepository.deleteByEmail(email);

        String newGeneratedCode = generateSixDigitCode();

        ConfirmationCode newCode = new ConfirmationCode();
        newCode.setEmail(email);
        newCode.setCode(newGeneratedCode);
        newCode.setExpirationDate(LocalDateTime.now().plusMinutes(10));
        confirmationCodeRepository.save(newCode);

        emailService.sendCodeEmail(email, newGeneratedCode, role);
    }

    public static String generateSixDigitCode() {
        Random random = new Random();
        int code = random.nextInt(900000) + 100000;
        return String.valueOf(code);
    }
}
