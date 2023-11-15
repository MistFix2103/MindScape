/**
 * <p>Описание:</p>
 * Контроллер для обработки запросов, связанных с изменением данных учетной записи.
 * Обрабатывает запросы по адресу /home/profile.
 *
 * <p>Список методов:</p>
 * <ul>
 *     <li><b>deleteAccount</b> - Удаляет аккаунт пользователя.</li>
 *     <li><b>changeName</b> - Меняет имя пользователя.</li>
 *     <li><b>preChangeMail</b> - Вызывает сервисный метод для проверки почты.</li>
 *     <li><b>changeMail</b> - Вызывает сервисный метод для изменения почты.</li>
 *     <li><b>preChangePass</b> - Вызывает сервисный метод для проверки пароля.</li>
 *     <li><b>changePass</b> - Вызывает сервисный метод для проверки пароля.</li>
 *     <li><b>resendCode</b> - Метод для повторной отправки кода.</li>
 * </ul>
 */

package ru.mtuci.MindScape.home.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.mtuci.MindScape.auth.service.UserService;
import ru.mtuci.MindScape.home.service.ProfileEditService;
import ru.mtuci.MindScape.user.repository.UserRepository;

@Controller
@RequestMapping("/home/profile")
@AllArgsConstructor
public class ProfileEditController {
    private final UserService userService;
    private final UserRepository userRepository;
    private final ProfileEditService profileEditService;
    @PostMapping("/delete-account")
    public String deleteAccount(Authentication authentication, RedirectAttributes redirectAttributes) {
        userRepository.deleteByEmail(authentication.getName());
        redirectAttributes.addFlashAttribute("message", "Ваш аккаунт удален!");
        return "redirect:/login";
    }

    @PostMapping("/change-name")
    public String changeName(
            @RequestParam("name") String newName,
            Authentication authentication,
            RedirectAttributes redirectAttributes,
            HttpServletRequest request,
            HttpSession session) {
        profileEditService.changeName(newName, authentication.getName(), session);
        redirectAttributes.addFlashAttribute("highlightContainerName", "name-container");
        return "redirect:" + request.getHeader("Referer");
    }

    @PostMapping("/mail_change")
    private String preChangeMail(@RequestParam("mail") String newMail, RedirectAttributes redirectAttributes, HttpSession session) {
        profileEditService.preChangeMail(newMail);
        redirectAttributes.addFlashAttribute("operationType", "mail_change");
        session.setAttribute("newMail", newMail);
        return "redirect:/home/profile/verification";
    }

    @PostMapping("/verification/mail_change")
    public String changeMail(
            @RequestParam String code,
            Authentication authentication,
            RedirectAttributes redirectAttributes,
            HttpServletRequest request,
            HttpSession session) {
        request.setAttribute("operationType", "mail_change");
        String oldEmail = authentication.getName();
        String newEmail = (String) session.getAttribute("newMail");
        userService.validateCode(newEmail, code);
        profileEditService.changeMail(oldEmail, newEmail);
        session.removeAttribute("newMail");
        redirectAttributes.addFlashAttribute("highlightContainerName", "email-container");
        return "redirect:/home/profile";
    }

    @PostMapping("/pass_change")
    public String preChangePass(
            @RequestParam("password") String newPass,
            @RequestParam("confirmPassword") String confirmPass,
            Authentication authentication,
            RedirectAttributes redirectAttributes,
            HttpSession session) {
        String email = authentication.getName();
        profileEditService.preChangePass(newPass, confirmPass, email);
        session.setAttribute("newPass", newPass);
        redirectAttributes.addFlashAttribute("operationType", "password_change");
        return "redirect:/home/profile/verification";
    }

    @PostMapping("/verification/password_change")
    public String changePass(
            @RequestParam String code,
            Authentication authentication,
            RedirectAttributes redirectAttributes,
            HttpServletRequest request,
            HttpSession session){
        request.setAttribute("operationType", "password_change");
        String email = authentication.getName();
        userService.validateCode(email, code);
        profileEditService.changePass(email, (String) session.getAttribute("newPass"));
        session.removeAttribute("newPass");
        redirectAttributes.addFlashAttribute("highlightContainerName", "pass-container");
        return "redirect:/home/profile";
    }

    @PostMapping("/verification/resendCode")
    public String resendCode(
            @RequestParam("operation") String operation,
            RedirectAttributes redirectAttributes,
            Authentication authentication,
            HttpSession session) {
        if (operation.equals("mail_change")) {
            String newMail = (String) session.getAttribute("newMail");
            redirectAttributes.addFlashAttribute("operationType", "mail_change");
            userService.createAndSendCode(newMail, "mail_change");
        } else if (operation.equals("password_change")) {
            redirectAttributes.addFlashAttribute("operationType", "password_change");
            userService.createAndSendCode(authentication.getName(), "pass_change");
        }
        redirectAttributes.addFlashAttribute("message", "Код был повторно отправлен.");
        return "redirect:/home/profile/verification";
    }
}