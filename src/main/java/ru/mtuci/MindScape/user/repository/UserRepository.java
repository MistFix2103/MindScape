/**
 * <p>Описание:</p>
 * Репозиторий для работы с пользователями. Наследует интерфейс JpaRepository.
 *
 * <p>Методы:</p>
 * <ul>
 *     <li><b>findByEmail</b> - Поиск пользователя по электронной почте.</li>
 * </ul>
 */

package ru.mtuci.MindScape.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mtuci.MindScape.user.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
}
