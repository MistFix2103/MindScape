/**
 * <p>Описание:</p>
 * Класс DTO для регистрации экспертов, наследующийся от базового класса BaseRegistrationDto.
 * Добавляет поле для хранения документов, которые могут быть прикреплены к экспертному профилю.
 */

package ru.mtuci.MindScape.auth_reg.dto;

import jakarta.persistence.Lob;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExpertRegistrationDto extends BaseRegistrationDto {
    @Lob
    private byte[] document;
}
