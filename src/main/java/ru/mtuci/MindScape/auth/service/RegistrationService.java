package ru.mtuci.MindScape.auth.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mtuci.MindScape.auth.dto.UserRegistrationDto;
import ru.mtuci.MindScape.user.model.ConfirmationCode;
import ru.mtuci.MindScape.user.model.User;
import ru.mtuci.MindScape.user.model.UserRole;
import ru.mtuci.MindScape.user.repository.ConfirmationCodeRepository;
import ru.mtuci.MindScape.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Random;

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

        String confirmationCode = generateSixDigitCode();
        ConfirmationCode codeEntity = new ConfirmationCode();
        codeEntity.setEmail(registrationDto.getEmail());
        codeEntity.setCode(confirmationCode);
        codeEntity.setExpirationDate(LocalDateTime.now().plusMinutes(10));
        codeEntity.setType(ConfirmationCode.Type.USER_REGISTRATION);
        confirmationCodeRepository.save(codeEntity);

        emailService.sendCodeEmail(registrationDto.getEmail(), confirmationCode, 1);
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
        storedCodeEntity.setConfirmed(true);
        confirmationCodeRepository.delete(storedCodeEntity);
    }

    private void validateRegistration(UserRegistrationDto registrationDto) {
        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new RuntimeException("EmailExists");
        }

        if (registrationDto.getPassword().length() < 6) {
            throw new RuntimeException("PasswordTooShort");
        }

        if (!registrationDto.getPassword().equals(registrationDto.getConfirmPassword())) {
            throw new RuntimeException("PasswordsDoNotMatch");
        }
    }

    @Transactional
    public void validateCode(String email, String code) throws RuntimeException {
        ConfirmationCode storedCodeEntity = confirmationCodeRepository.findByEmail(email);

        if (storedCodeEntity == null) {
            throw new RuntimeException("InvalidCode");
        }

        LocalDateTime now = LocalDateTime.now();
        if (storedCodeEntity.getExpirationDate().isBefore(now)) {
            throw new RuntimeException("CodeExpired");
        }

        if (!storedCodeEntity.getCode().equals(code)) {
            throw new RuntimeException("InvalidCode");
        }
    }

    public void resendCode(String email) {
        confirmationCodeRepository.deleteByEmail(email);

        String newGeneratedCode = generateSixDigitCode();

        ConfirmationCode newCode = new ConfirmationCode();
        newCode.setEmail(email);
        newCode.setCode(newGeneratedCode);
        newCode.setExpirationDate(LocalDateTime.now().plusMinutes(10));
        confirmationCodeRepository.save(newCode);

        emailService.sendCodeEmail(email, newGeneratedCode, 1);
    }

    public static String generateSixDigitCode() {
        Random random = new Random();
        int code = random.nextInt(900000) + 100000;
        return String.valueOf(code);
    }
}
