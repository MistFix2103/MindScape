/**
 * <p>Описание:</p>
 * Контроллер для обработки запросов, связанных с регистрацией пользователей.
 * Обрабатывает запросы по адресу /registration.
 *
 * <p>Список методов:</p>
 * <ul>
 *     <li>
 *         <b>preRegisterUser</b> - Предварительная регистрация обычного пользователя.
 *         <ul>
 *             <li>Вызывает метод предварительной регистрации с учетом того, что регистрируется пользователь.</li>
 *         </ul>
 *     </li>
 *     <li>
 *         <b>preRegisterExpert</b> - Предварительная регистрация психолога.
 *         <ul>
 *             <li>Вызывает метод предварительной регистрации с учетом того, что регистрируется психолог или исследователь.</li>
 *         </ul>
 *     </li>
 *     <li>
 *         <b>preRegister</b> - Общий метод для предварительной регистрации.
 *         <ul>
 *             <li>Вызывается внутри других методов preRegister для общей логики регистрации.</li>
 *              <li>Проверяет данные перед регистрацией и сохраняет их в сессии.</li>
 *             <li>Обрабатывает исключения, связанные с некорректностью введенных данных.</li>
 *         </ul>
 *     </li>
 *     <li>
 *         <b>register</b> - Финальный этап регистрации.
 *         <ul>
 *             <li>Проверяет код подтверждения и завершает регистрацию в случае успеха.</li>
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

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.mtuci.MindScape.auth_reg.dto.BaseRegistrationDto;
import ru.mtuci.MindScape.auth_reg.dto.ExpertRegistrationDto;
import ru.mtuci.MindScape.auth_reg.dto.UserRegistrationDto;
import ru.mtuci.MindScape.auth_reg.service.RegistrationService;
import ru.mtuci.MindScape.auth_reg.session.UserSession;

import javax.validation.Valid;
import java.io.IOException;

import static ru.mtuci.MindScape.exceptions.CustomExceptions.*;

@Controller
@RequestMapping("/registration")
@AllArgsConstructor
public class RegistrationController {
    private final RegistrationService registrationService;
    private final UserSession userSession;

    @PostMapping("/user")
    public String preRegisterUser(@Valid @ModelAttribute UserRegistrationDto registrationDto, RedirectAttributes redirectAttributes) {
        userSession.setRole("user");
        userSession.setRegistrationDto(registrationDto);
        return preRegister(redirectAttributes);
    }

    @PostMapping({"/expert", "/researcher"})
    public String preRegisterExpert(
            @Valid @ModelAttribute ExpertRegistrationDto registrationDto,
            @RequestParam("file") MultipartFile documents,
            RedirectAttributes redirectAttributes,
            HttpServletRequest request) throws IOException {
        registrationDto.setDocument(documents.getBytes());
        String role = request.getServletPath().contains("/expert") ? "expert" : "researcher";
        userSession.setRole(role);
        userSession.setRegistrationDto(registrationDto);
        return preRegister(redirectAttributes);
    }

    private String preRegister(RedirectAttributes redirectAttributes) {
        String role = userSession.getRole();
        try {
            registrationService.preRegister();
            redirectAttributes.addAttribute("role", role);
            return "redirect:/registration/verification";
        } catch (EmailExistsException | PasswordTooShortException | PasswordsDoNotMatchException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/registration/" + role;
        } catch (Exception e) {
            System.err.println("Неизвестная ошибка: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Неизвестная ошибка!");
            return "redirect:/registration/" + role;
        }
    }

    @PostMapping("/verification")
    public String register(@RequestParam String code, Model model, RedirectAttributes redirectAttributes) {
        BaseRegistrationDto registrationDtoFromSession = userSession.getRegistrationDto();
        registrationDtoFromSession.setCode(code);
        try {
            registrationService.validateCode(registrationDtoFromSession.getEmail(), registrationDtoFromSession.getCode());
            registrationService.register();
            redirectAttributes.addFlashAttribute("message", "Вы успешно зарегистрировались!");
            return "redirect:/login";
        } catch (InvalidCodeException | CodeExpiredException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("operationType", "registration");
            return "verification";
        }
    }

    @GetMapping("/resendCode")
    public String resendCode(Model model) {
        model.addAttribute("operationType", "registration");
        BaseRegistrationDto registrationDto = userSession.getRegistrationDto();
        String role = registrationDto instanceof ExpertRegistrationDto ? "expert" : "user";
        try {
            registrationService.resendCode(registrationDto.getEmail(), role);
        } catch (Exception e) {
            model.addAttribute("error", "Произошла ошибка при повторной отправке кода.");
            return "verification";
        }
        return "verification";
    }
}