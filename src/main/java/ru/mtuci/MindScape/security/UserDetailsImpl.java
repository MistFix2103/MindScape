/**
 * <p>Описание:</p>
 * Класс реализует интерфейс UserDetails, необходимый для интеграции с Spring Security.
 *
 * <p>Поля:</p>
 * <ul>
 *     <li>username: Имя пользователя.</li>
 *     <li>password: Пароль.</li>
 *     <li>authorities: Роли и права пользователя.</li>
 *     <li>isActive: Статус активности аккаунта.</li>
 * </ul>
 *
 * <p>Методы:</p>
 * <ul>
 *     <li><b>isAccountNonExpired, isAccountNonLocked, isCredentialsNonExpired, isEnabled</b> -
 *     Методы для проверки статуса аккаунта.</li>
 * </ul>
 */

package ru.mtuci.MindScape.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

@Data
@AllArgsConstructor
public class UserDetailsImpl implements UserDetails {

    private final String username;
    private final String password;
    private final List<SimpleGrantedAuthority> authorities;
    private final boolean isActive;

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}