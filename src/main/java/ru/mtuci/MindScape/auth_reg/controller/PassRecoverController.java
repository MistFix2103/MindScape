/**
 * <p>Описание:</p>
 * Контроллер для обработки запросов, связанных с восстановлением пароля пользователя.
 * Обрабатывает запросы по адресу /forgot_password.
 *
 * <p>Список методов:</p>
 * <ul>
 *     <li>
 *         <b>preRecover</b> - Получает данные от пользователя для восстановления пароля.
 *         <ul>
 *             <li>Вызывает сервис для предварительной проверки данных.</li>
 *             <li>В случае успешной проверки перенаправляет пользователя на страницу для ввода кода подтверждения.</li>
 *             <li>Обрабатывает исключения, связанные с неверными данными, и возвращает сообщение об ошибке.</li>
 *         </ul>
 *     </li>
 *     <li>
 *         <b>recover</b> - Выполняет восстановление пароля.
 *         <ul>
 *             <li>Проверяет код подтверждения и меняет пароль в случае успеха.</li>
 *             <li>Обрабатывает исключения, связанные с недействительным или истекшим кодом.</li>
 *         </ul>
 *     </li>
 *     <li>
 *         <b>resendCode</b> - Повторно отправляет код подтверждения на почту пользователя.
 *         <ul>
 *             <li>В случае ошибки возвращает соответствующее сообщение.</li>
 *         </ul>
 *     </li>
 * </ul>
 */

package ru.mtuci.MindScape.auth_reg.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.mtuci.MindScape.auth_reg.dto.PassRecoverDto;
import ru.mtuci.MindScape.auth_reg.service.PassRecoverService;
import ru.mtuci.MindScape.auth_reg.service.RegistrationService;
import ru.mtuci.MindScape.auth_reg.session.UserSession;

import javax.validation.Valid;

import static ru.mtuci.MindScape.exceptions.CustomExceptions.*;

@Controller
@RequestMapping("/forgot_password")
@AllArgsConstructor
public class PassRecoverController {
    private final PassRecoverService passService;
    private final RegistrationService registrationService;
    private final UserSession userSession;

    @PostMapping
    public String preRecover(@Valid @ModelAttribute PassRecoverDto passRecoverDto, Model model) {
        try {
            userSession.setPassRecoverDto(passRecoverDto);
            passService.preChange();
            return "redirect:/forgot_password/verification";
        } catch (PasswordsDoNotMatchException | PasswordTooShortException | NewPassCanNotMatchOldPassException | EmailDoesNotExistException e) {
            model.addAttribute("error", e.getMessage());
            return "forgot_password";
        } catch (Exception e) {
            System.err.println("Неизвестная ошибка: " + e.getMessage());
            model.addAttribute("error", "Неизвестная ошибка!");
            return "forgot_password";
        }
    }

    @PostMapping("/verification")
    public String recover(@Valid @ModelAttribute PassRecoverDto passRecoverDto, Model model, RedirectAttributes redirectAttributes){
        PassRecoverDto passDtoFromSession = userSession.getPassRecoverDto();

        if (passDtoFromSession == null) {
            model.addAttribute("error", "Произошла ошибка сессии. Попробуйте заново.");
            model.addAttribute("operationType", "recovery");
            return "verification";
        }

        passDtoFromSession.setCode(passRecoverDto.getCode());
        try {
            registrationService.validateCode(passDtoFromSession.getEmail(), passDtoFromSession.getCode());
            passService.recover();
            redirectAttributes.addFlashAttribute("message", "Пароль успешно изменен!");
            return "redirect:/login";
        } catch (InvalidCodeException | CodeExpiredException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("operationType", "recovery");
            return "verification";
        } catch (Exception e) {
            System.err.println("Неизвестная ошибка: " + e.getMessage());
            model.addAttribute("error", "Неизвестная ошибка!");
            model.addAttribute("operationType", "recovery");
            return "verification";
        }
    }

    @GetMapping("/resendCode")
    public String resendCode(Model model) {
        model.addAttribute("operationType", "recovery");
        PassRecoverDto passRecoverDto = userSession.getPassRecoverDto();
        try {
            registrationService.resendCode(passRecoverDto.getEmail(), "recover");
        } catch (Exception e) {
            System.err.println("Неизвестная ошибка: " + e.getMessage());
            model.addAttribute("error", "Произошла ошибка при повторной отправке кода.");
            return "verification";
        }
        return "verification";
    }
}
