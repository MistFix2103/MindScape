/**
 * <p>Описание:</p>
 * Перечисление для представления ролей пользователей и их разрешений.
 *
 * <p>Поля:</p>
 * <ul>
 *     <li>permissions: Набор разрешений для роли.</li>
 * </ul>
 *
 * <p>Методы:</p>
 * <ul>
 *     <li><b>grantedAuthority</b> - Возвращает набор разрешений в виде объектов SimpleGrantedAuthority.</li>
 * </ul>
 */

package ru.mtuci.MindScape.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import ru.mtuci.MindScape.common.Permission;

import java.util.Set;

import static java.util.stream.Collectors.toSet;

@Getter
@AllArgsConstructor
public enum UserRole {
    USER(Set.of(Permission.READ)),
    EXPERT(Set.of(Permission.READ)),
    RESEARCHER(Set.of(Permission.READ));

    private final Set<Permission> permissions;

    public Set<SimpleGrantedAuthority> grantedAuthority() {
        return getPermissions().stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(toSet());
    }
}
