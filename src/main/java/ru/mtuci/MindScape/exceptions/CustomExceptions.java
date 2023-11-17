/**
 * <p>Описание:</p>
 * Класс для обработки кастомных исключений.
 */

package ru.mtuci.MindScape.exceptions;

import lombok.Getter;

public class CustomExceptions {

    @Getter
    public static class EmailExistsException extends RuntimeException {
        private final String message = "Пользователь с такой почтой уже существует!";
    }

    @Getter
    public static class EmailDoesNotExistException extends RuntimeException {
        private final String message = "Пользователя с такой почтой не существует!";
    }

    @Getter
    public static class PasswordTooShortException extends RuntimeException {
        private final String message = "Пароль слишком короткий!";
    }

    @Getter
    public static class PasswordsDoNotMatchException extends RuntimeException {
        private final String message = "Пароли не совпадают!";
    }

    @Getter
    public static class InvalidCodeException extends RuntimeException {
        private final String message = "Неверный код!";
    }

    @Getter
    public static class CodeExpiredException extends RuntimeException {
        private final String message = "Код устарел!";
    }

    @Getter
    public static class NewPassCanNotMatchOldPassException extends RuntimeException {
        private final String message = "Новый пароль не может совпадать со старым!";
    }

    @Getter
    public static class NameIsTooLongException extends RuntimeException {
        private final String message = "Имя слишком длинное (>24 символов)!";
    }

    @Getter
    public static class IncorrectNameException extends RuntimeException {
        private final String message = "Имя может содержать только буквы!";
    }
}

