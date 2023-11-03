/**
 * <p>Описание:</p>
 * Компонент для управления данными сессии пользователя во время работы веб-приложения.
 * Объект этого класса живет в рамках HTTP-сессии и хранит информацию, связанную с текущим пользовательским сеансом.
 *
 * <p>Атрибуты класса:</p>
 * <ul>
 *     <li><b>registrationDto</b> - DTO, содержащий данные, введенные пользователем при регистрации.</li>
 *     <li><b>passRecoverDto</b> - DTO, используемое для хранения данных, связанных с процессом восстановления пароля.</li>
 *     <li><b>role</b> - Строка, обозначающая роль пользователя в текущей сессии ("user", "expert" или "researcher").</li>
 * </ul>
 *
 * Использование класса с аннотацией {@code @Scope("session")} гарантирует, что для каждой пользовательской сессии будет создан
 * уникальный экземпляр этого компонента. Аннотация {@code @ScopedProxyMode.TARGET_CLASS} позволяет внедрять
 * прокси-объект этого компонента в бины с областью видимости 'singleton', обеспечивая корректный доступ к данным сессии
 * в любой части приложения.
 */

package ru.mtuci.MindScape.auth_reg.session;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import ru.mtuci.MindScape.auth_reg.dto.BaseRegistrationDto;
import ru.mtuci.MindScape.auth_reg.dto.PassRecoverDto;

@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
@Getter
@Setter
public class UserSession {
    private BaseRegistrationDto registrationDto;
    private PassRecoverDto passRecoverDto;
    private String role;
}
