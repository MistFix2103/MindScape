package ru.mtuci.MindScape.auth_reg.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mtuci.MindScape.auth_reg.dto.UserRegistrationDto;
import ru.mtuci.MindScape.user.model.ConfirmationCode;
import ru.mtuci.MindScape.user.model.User;
import ru.mtuci.MindScape.user.model.UserRole;
import ru.mtuci.MindScape.user.repository.ConfirmationCodeRepository;
import ru.mtuci.MindScape.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Random;

import static ru.mtuci.MindScape.exceptions.CustomExceptions.*;

@Service
@AllArgsConstructor
@Getter
public class RegistrationService {

    private final UserRepository userRepository;
    private final ConfirmationCodeRepository confirmationCodeRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public void preRegister(UserRegistrationDto registrationDto) {
        validateRegistration(registrationDto);
        String email = registrationDto.getEmail();

        confirmationCodeRepository.deleteByEmail(email);
        String confirmationCode = generateSixDigitCode();
        ConfirmationCode codeEntity = new ConfirmationCode();
        codeEntity.setEmail(email);
        codeEntity.setCode(confirmationCode);
        codeEntity.setExpirationDate(LocalDateTime.now().plusMinutes(10));
        codeEntity.setType(ConfirmationCode.Type.USER_REGISTRATION);
        confirmationCodeRepository.save(codeEntity);

        emailService.sendCodeEmail(email, confirmationCode, 1);
    }

    @Transactional
    public void register(UserRegistrationDto registrationDto) {
        ConfirmationCode storedCodeEntity = confirmationCodeRepository.findByEmail(registrationDto.getEmail());

        User user = new User();
        user.setUsername(registrationDto.getName());
        user.setEmail(registrationDto.getEmail());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        user.setRole(UserRole.USER);

        userRepository.save(user);
        confirmationCodeRepository.delete(storedCodeEntity);
    }

    private void validateRegistration(UserRegistrationDto registrationDto) {
        if (userRepository.existsByEmail(registrationDto.getEmail())) {
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

    public void resendCode(String email, int role) {
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
