/**
 * <p>Описание:</p>
 * Репозиторий для работы с исследователями. Наследует интерфейс JpaRepository.
 *
 * <p>Методы:</p>
 * <ul>
 *     <li><b>findByEmail</b> - Поиск исследователя по электронной почте.</li>
 * </ul>
 */

package ru.mtuci.MindScape.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mtuci.MindScape.user.model.Researcher;

import java.util.Optional;
import java.util.UUID;

public interface ResearcherRepository extends JpaRepository<Researcher, UUID> {
    Optional<Researcher> findByEmail(String email);
}
