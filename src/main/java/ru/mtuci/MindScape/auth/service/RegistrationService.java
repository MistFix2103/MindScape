package ru.mtuci.MindScape.auth.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mtuci.MindScape.auth.dto.UserRegistrationDto;
import ru.mtuci.MindScape.user.model.User;
import ru.mtuci.MindScape.user.model.UserRole;
import ru.mtuci.MindScape.user.repository.UserRepository;

@Service
@AllArgsConstructor
@Getter
public class RegistrationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void register(UserRegistrationDto registrationDto) {
        validateRegistration(registrationDto);

        User user = new User();
        user.setUsername(registrationDto.getName());
        user.setEmail(registrationDto.getEmail());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        user.setRole(UserRole.USER);

        userRepository.save(user);
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
}
