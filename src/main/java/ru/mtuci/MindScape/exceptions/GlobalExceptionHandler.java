/**
 * Класс GlobalExceptionHandler обрабатывает исключения на уровне всего приложения и логирует их.
 */

package ru.mtuci.MindScape.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static ru.mtuci.MindScape.exceptions.CustomExceptions.*;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public String handleException(Exception e, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        log.error("Неизвестная ошибка: " + e.getMessage(), e);
        redirectAttributes.addFlashAttribute("error", "Неизвестная ошибка!");
        return "redirect:" + request.getHeader("Referer");
    }

    @ExceptionHandler({
            EmailDoesNotExistException.class,
            EmailExistsException.class,
            PasswordTooShortException.class,
            PasswordsDoNotMatchException.class,
            NewPassCanNotMatchOldPassException.class,
            IncorrectNameException.class,
            NameIsTooLongException.class
    })
    public String handleCredentialsExceptions(Exception e, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        log.error("Ошибка проверки данных: " + e.getMessage(), e);
        String attributeKey = "error";
        if (referer.contains("/home/profile")) {
            if (e instanceof IncorrectNameException || e instanceof NameIsTooLongException) {
                attributeKey = "nameError";
            } else if (e instanceof EmailExistsException) {
                attributeKey = "emailError";
            } else {
                attributeKey = "passError";
            }
        }
        redirectAttributes.addFlashAttribute(attributeKey, e.getMessage());
        return "redirect:" + referer;
    }

    @ExceptionHandler({InvalidCodeException.class, CodeExpiredException.class})
    public String handleCodeExceptions(Exception e, HttpServletRequest request, Model model) {
        log.error("Ошибка ввода кода: " + e.getMessage(), e);
        String operationType = (String) request.getAttribute("operationType");
        model.addAttribute("error", e.getMessage());
        model.addAttribute("operationType", operationType);
        return "verification";
    }
}
