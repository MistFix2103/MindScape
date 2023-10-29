package ru.mtuci.MindScape.auth_reg.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mtuci.MindScape.auth_reg.dto.PassRecoverDto;
import ru.mtuci.MindScape.user.model.ConfirmationCode;
import ru.mtuci.MindScape.user.model.User;
import ru.mtuci.MindScape.user.repository.ConfirmationCodeRepository;
import ru.mtuci.MindScape.user.repository.UserRepository;

import java.time.LocalDateTime;

import static ru.mtuci.MindScape.auth_reg.service.RegistrationService.generateSixDigitCode;
import static ru.mtuci.MindScape.exceptions.CustomExceptions.*;

@Service
@AllArgsConstructor
@Getter
public class PassRecoverService {

    private final UserRepository userRepository;
    private final ConfirmationCodeRepository confirmationCodeRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public void preChange(PassRecoverDto passRecoverDto) {
        validate(passRecoverDto);
        String email = passRecoverDto.getEmail();

        confirmationCodeRepository.deleteByEmail(email);
        String confirmationCode = generateSixDigitCode();
        ConfirmationCode codeEntity = new ConfirmationCode();
        codeEntity.setEmail(email);
        codeEntity.setCode(confirmationCode);
        codeEntity.setExpirationDate(LocalDateTime.now().plusMinutes(10));
        codeEntity.setType(ConfirmationCode.Type.PASSWORD_RESET);
        confirmationCodeRepository.save(codeEntity);

        emailService.sendCodeEmail(email, confirmationCode, 4);
    }

    public void validate(PassRecoverDto passRecoverDto) {
        if (!userRepository.existsByEmail(passRecoverDto.getEmail())) {
            throw new EmailDoesNotExistException();
        }

        if (passRecoverDto.getPassword().length() < 6) {
            throw new PasswordTooShortException();
        }

        if (!passRecoverDto.getPassword().equals(passRecoverDto.getConfirmPassword())) {
            throw new PasswordsDoNotMatchException();
        }

        String hashedOldPass = userRepository.findByEmail(passRecoverDto.getEmail()).get().getPassword();
        if (passwordEncoder.matches(passRecoverDto.getPassword(), hashedOldPass)) {
            throw new NewPassCanNotMatchOldPassException();
        }
    }

    @Transactional
    public void recover(PassRecoverDto passRecoverDto) {
        User user = userRepository.findByEmail(passRecoverDto.getEmail()).get();
        ConfirmationCode storedCodeEntity = confirmationCodeRepository.findByEmail(passRecoverDto.getEmail());

        String encodedPassword = passwordEncoder.encode(passRecoverDto.getPassword());
        user.setPassword(encodedPassword);
        userRepository.save(user);

        confirmationCodeRepository.delete(storedCodeEntity);
    }
}
