/**
 * <p>Описание:</p>
 * Контроллер для обработки запросов, связанных с регистрацией пользователей.
 * Обрабатывает запросы по адресу /registration.
 *
 * <p>Поля:</p>
 * <ul>
 *     <li>registrationService: Сервис, который содержит логику для регистрации пользователей.</li>
 *     <li>userService: Сервис, обрабатывающий общие операции с пользователями.</li>
 * </ul>
 *
 * <p>Список методов:</p>
 * <ul>
 *     <li><b>preRegisterUser</b> - Если регистрируется психолог или эксперт, устанавливает документы, вызывает сервисный метод для проверки данных.</li>
 *     <li><b>register</b> - Вызывает сервисный метод, который проверяет ответ на капчу.</li>
 *     <li><b>confirmCaptcha</b> - Повторно отправляет код подтверждения на почту пользователя.</li>
 *     <li><b>resendCode</b> - Повторно отправляет код подтверждения на почту пользователя.</li>
 * </ul>
 */

package ru.mtuci.MindScape.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.mtuci.MindScape.auth.dto.UserRegistrationDto;
import ru.mtuci.MindScape.auth.service.RegistrationService;
import ru.mtuci.MindScape.auth.service.UserService;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Map;

@Controller
@RequestMapping("/registration")
@AllArgsConstructor
public class RegistrationController {
    private final RegistrationService registrationService;
    private final UserService userService;

    @PostMapping({"/user", "/expert", "/researcher"})
    @ResponseBody
    public ResponseEntity<?> preRegisterUser(
            @Valid @ModelAttribute UserRegistrationDto regDto,
            @RequestParam(name = "file", required = false) MultipartFile documents,
            HttpServletRequest request,
            HttpSession session) throws IOException {
        String role = request.getServletPath().replace("/registration/", "");
        regDto.setRole(role);
        if (!"user".equals(role) && documents != null) {
            regDto.setDocument(documents.getBytes());
        }
        session.setAttribute("DTO", regDto);
        registrationService.preRegister(session);
        return ResponseEntity.ok(userService.generateCaptcha(session));
    }

    @PostMapping("/confirmCaptcha")
    @ResponseBody
    public ResponseEntity<?> confirmCaptcha(
            @RequestParam("captcha") String answer,
            HttpSession session) {
        UserRegistrationDto regDto = (UserRegistrationDto) session.getAttribute("DTO");
        String role = regDto.getRole();
        userService.checkCaptcha((int) session.getAttribute("num1"), (int) session.getAttribute("num2"), Integer.parseInt(answer), role, regDto.getEmail());
        return ResponseEntity.ok(Map.of("role", role));
    }

    @PostMapping("/verification")
    public String register(@RequestParam String code, RedirectAttributes redirectAttributes, HttpServletRequest request, HttpSession session) {
        request.setAttribute("operationType", "registration");
        UserRegistrationDto regDto = (UserRegistrationDto) session.getAttribute("DTO");
        userService.validateCode(regDto.getEmail(), code);
        registrationService.register(session);
        redirectAttributes.addFlashAttribute("message", "Вы успешно зарегистрировались!");
        return "redirect:/login";
    }

    @PostMapping("/resendCode")
    public String resendCode(HttpServletRequest request, RedirectAttributes redirectAttributes, HttpSession session) {
        UserRegistrationDto regDto = (UserRegistrationDto) session.getAttribute("DTO");
        userService.createAndSendCode(regDto.getEmail(), regDto.getRole());
        redirectAttributes.addFlashAttribute("operationType", "registration");
        redirectAttributes.addFlashAttribute("message", "Код был повторно отправлен.");
        return "redirect:" + request.getHeader("Referer");
    }
}