/**
 * <p>Описание:</p>
 * Класс для представления пользователя.
 */
package ru.mtuci.MindScape.user.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "users")
@AllArgsConstructor
public class User extends BaseUser{

}