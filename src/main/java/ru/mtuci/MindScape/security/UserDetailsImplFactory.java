/**
 * <p>Описание:</p>
 * Фабричный класс для создания объектов UserDetails из различных типов пользователей (User, Expert, Researcher).
 *
 * <p>Методы:</p>
 * <ul>
 *     <li><b>fromUser</b> - Создает объект UserDetails из объекта User.</li>
 *     <li><b>fromExpert</b> - Создает объект UserDetails из объекта Expert.</li>
 *     <li><b>fromResearcher</b> - Создает объект UserDetails из объекта Researcher.</li>
 * </ul>
 */

package ru.mtuci.MindScape.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.mtuci.MindScape.user.model.Expert;
import ru.mtuci.MindScape.user.model.Researcher;
import ru.mtuci.MindScape.user.model.User;

import java.util.Collections;
import java.util.List;

public class UserDetailsImplFactory {

    public static UserDetails fromUser(User user) {
        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("USER"));
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                true,
                true,
                true,
                true,
                authorities);
    }

    public static UserDetails fromExpert(Expert expert) {
        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("EXPERT"));
        return new org.springframework.security.core.userdetails.User(
                expert.getEmail(),
                expert.getPassword(),
                true,
                true,
                true,
                true,
                authorities);
    }

    public static UserDetails fromResearcher(Researcher researcher) {
        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("RESEARCHER"));
        return new org.springframework.security.core.userdetails.User(
                researcher.getEmail(),
                researcher.getPassword(),
                true,
                true,
                true,
                true,
                authorities);
    }
}