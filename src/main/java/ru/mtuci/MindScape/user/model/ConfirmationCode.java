/**
 * <p>Описание:</p>
 * Класс для представления кода подтверждения в системе.
 *
 * <p>Поля:</p>
 * <ul>
 *     <li>id: Уникальный идентификатор.</li>
 *     <li>code: Код подтверждения.</li>
 *     <li>expirationDate: Дата истечения срока действия кода.</li>
 *     <li>email: Электронная почта, к которой привязан код.</li>
 *     <li>type: Тип кода подтверждения.</li>
 * </ul>
 */

package ru.mtuci.MindScape.user.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
public class ConfirmationCode {
    public enum Type {
        USER_REGISTRATION,
        EXPERT_REGISTRATION,
        PASSWORD_RESET,
        CHANGE_EMAIL,
        CHANGE_PASSWORD,
        TWO_FACTOR;
    }

    @Id
    @GeneratedValue
    private UUID id;
    private String code;
    private LocalDateTime expirationDate;
    private String email;

    @Enumerated(EnumType.STRING)
    private Type type;
}
