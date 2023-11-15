/**
 * <p>Описание:</p>
 * Контроллер для обработки запросов, связанных с восстановлением пароля пользователя.
 * Обрабатывает запросы по адресу /forgot_password.
 *
 * <p>Поля:</p>
 * <ul>
 *     <li>passService: Сервис, который содержит логику для изменения пароля пользователя, который не может войти в систему.</li>
 *     <li>userService: Сервис, обрабатывающий общие операции с пользователями.</li>
 * </ul>
 *
 * <p>Список методов:</p>
 * <ul>
 *     <li><b>preRecover</b> - Вызывает сервисный метод для проверки данных.</li>
 *     <li><b>recover</b> - Вызывает сервисный метод, который меняет пароль и сохраняет изменения в репозитории.</li>
 *     <li><b>resendCode</b> - Повторно отправляет код подтверждения на почту пользователя. </li>
 * </ul>
 */

package ru.mtuci.MindScape.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.mtuci.MindScape.auth.dto.PassRecoverDto;
import ru.mtuci.MindScape.auth.service.PassRecoverService;
import ru.mtuci.MindScape.auth.service.UserService;

import javax.validation.Valid;

@Controller
@RequestMapping("/forgot_password")
@AllArgsConstructor
public class PassRecoverController {
    private final PassRecoverService passService;
    private final UserService userService;

    @PostMapping
    public String preRecover(@Valid @ModelAttribute PassRecoverDto passRecoverDto, HttpSession session) {
        session.setAttribute("PassDTO", passRecoverDto);
        passService.preChange(session);
        return "redirect:/forgot_password/verification";
    }

    @PostMapping("/verification")
    public String recover(
            @RequestParam String code,
            RedirectAttributes redirectAttributes,
            HttpSession session,
            HttpServletRequest request){
        request.setAttribute("operationType", "recovery");
        PassRecoverDto passDto = (PassRecoverDto) session.getAttribute("PassDTO");
        userService.validateCode(passDto.getEmail(), code);
        passService.recover(session);
        redirectAttributes.addFlashAttribute("message", "Пароль успешно изменен!");
        return "redirect:/login";
    }

    @PostMapping("/resendCode")
    public String resendCode(HttpServletRequest request, RedirectAttributes redirectAttributes, HttpSession session) {
        String email = ((PassRecoverDto)  session.getAttribute("PassDTO")).getEmail();
        userService.createAndSendCode(email, "recover");
        redirectAttributes.addFlashAttribute("operationType", "recovery");
        redirectAttributes.addFlashAttribute("message", "Код был повторно отправлен.");
        return "redirect:" + request.getHeader("Referer");
    }
}
