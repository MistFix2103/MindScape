/**
 * <p>Описание:</p>
 * Репозиторий для работы с экспертами. Наследует интерфейс JpaRepository.
 *
 * <p>Методы:</p>
 * <ul>
 *     <li><b>findByEmail</b> - Поиск эксперта по электронной почте.</li>
 * </ul>
 */

package ru.mtuci.MindScape.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mtuci.MindScape.user.model.Expert;

import java.util.Optional;
import java.util.UUID;

public interface ExpertRepository extends JpaRepository<Expert, UUID> {
    Optional<Expert> findByEmail(String email);
}
