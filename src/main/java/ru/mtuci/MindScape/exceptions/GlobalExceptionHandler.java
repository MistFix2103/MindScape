/**
 * Класс GlobalExceptionHandler обрабатывает исключения на уровне всего приложения и логирует их.
 */

package ru.mtuci.MindScape.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

import static ru.mtuci.MindScape.exceptions.CustomExceptions.*;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public void handleException(Exception e) {
        log.error("Неизвестная ошибка: " + e.getMessage(), e);
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
    @ResponseBody
    public ResponseEntity<?> handleCredentialsExceptions(Exception e) {
        log.error("Ошибка проверки данных: " + e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(IncorrectCaptchaCodeException.class)
    @ResponseBody
    public ResponseEntity<?> handleCaptchaException(Exception e, HttpSession session) {
        log.error("Ошибка проверки данных: " + e.getMessage(), e);
        Map<String, Object> response = new HashMap<>();
        response.put("captchaError", e.getMessage());
        response.put("num1", session.getAttribute("num1"));
        response.put("num2", session.getAttribute("num2"));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
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
