/**
 * <p>Описание:</p>
 * Репозиторий для работы с кодами подтверждения. Наследует интерфейс JpaRepository.
 *
 * <p>Методы:</p>
 * <ul>
 *     <li><b>findByEmail</b> - Поиск кода подтверждения по электронной почте.</li>
 *     <li><b>deleteByEmail</b> - Удаление кода подтверждения по электронной почте.</li>
 * </ul>
 */

package ru.mtuci.MindScape.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import ru.mtuci.MindScape.user.model.ConfirmationCode;

import java.util.UUID;

public interface ConfirmationCodeRepository extends JpaRepository<ConfirmationCode, UUID> {
    ConfirmationCode findByEmail(String email);

    @Transactional
    void deleteByEmail(String email);
}