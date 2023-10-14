package ru.mtuci.MindScape.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.mtuci.MindScape.user.model.User;
import ru.mtuci.MindScape.user.repository.UserRepository;

import java.util.Optional;

@Service
public class AuthenticationService {

    @Autowired
    private UserRepository userRepository; // ваш репозиторий для доступа к данным пользователя

    public boolean authenticate(String email, String password) {
        // Нахождение пользователя по email
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            return false; // Если пользователь не найден, возвращаем false
        }

        User user = userOptional.get();
        // Сравнение пароля (лучше использовать шифрованные пароли и сравнивать их после дешифрования)
        return user.getPassword().equals(password);
    }
}