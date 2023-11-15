/**
 * <p>Описание:</p>
 * Репозиторий для работы с пользователями. Наследует интерфейс JpaRepository.
 *
 * <p>Методы:</p>
 * <ul>
 *     <li><b>findByEmail</b> - Поиск пользователя по почте.</li>
 *     <li><b>deleteByEmail</b> - Удаление пользователя по почте.</li>
 * </ul>
 */

package ru.mtuci.MindScape.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import ru.mtuci.MindScape.user.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    @Transactional
    void deleteByEmail(String email);
}