/**
 * <p>Описание:</p>
 * Сервисная реализация интерфейса UserDetailsService для работы с данными пользователей.
 *
 * <p>Поле:</p>
 * <ul>
 *     <li><b>userRepository</b> - Репозиторий для работы с пользователями.</li>
 * </ul>
 *
 * <p>Методы:</p>
 * <ul>
 *     <li><b>loadUserByUsername</b> - Загрузка данных пользователя по электронной почте.</li>
 * </ul>
 */

package ru.mtuci.MindScape.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.mtuci.MindScape.user.model.User;
import ru.mtuci.MindScape.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            String role = String.valueOf(user.getRole());
            List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(role));
            return new org.springframework.security.core.userdetails.User(
                    user.getEmail(),
                    user.getPassword(),
                    true,
                    true,
                    true,
                    true,
                    authorities);
        } else {
            log.error("Не найден пользователь с почтой: {}", email);
            throw new UsernameNotFoundException("Не найден пользователь с почтой: " + email);
        }
    }
}