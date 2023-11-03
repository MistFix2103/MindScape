/**
 * <p>Описание:</p>
 * Класс для представления эксперта, расширяет базовый класс пользователя.
 *
 * <p><b>Поля:</b></p>
 * <ul>
 *     <li>document: Документ, подтверждающий статус эксперта.</li>
 *     <li>verified: Верификация аккаунта. По умолчанию имеет значение false.</li>
 * </ul>
 */

package ru.mtuci.MindScape.user.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.Set;
import java.util.UUID;

@EqualsAndHashCode(callSuper = false)
@Entity
@Data
@Table(name = "expert")
@AllArgsConstructor
@NoArgsConstructor
public class Expert extends BaseUser {
    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] document;

    @Column(name = "verified", nullable = false, columnDefinition = "TINYINT(1) default '0'")
    private boolean verified = false;
}