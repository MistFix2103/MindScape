/**
 * <p>Описание:</p>
 * Сервисный класс для отправки электронных писем. Поддерживает отправку писем с различными шаблонами в зависимости от типа операции.
 *
 * <p>Поля:</p>
 * <ul>
 *     <li>userRegistrationSubject, userRegistrationText: Тема и текст письма для регистрации обычных пользователей.</li>
 *     <li>expertRegistrationSubject, expertRegistrationText: Тема и текст письма для регистрации психологов и исследователей.</li>
 *     <li>passRecoverSubject, passRecoverText: Тема и текст письма для восстановления пароля.</li>
 * </ul>
 *
 * <p>Список методов:</p>
 * <ul>
 *     <li>
 *         <b>sendCodeEmail</b> - Отправляет письмо с кодом подтверждения, в зависимости от роли пользователя (обычный пользователь, эксперт или восстановление пароля).
 *     </li>
 *     <li>
 *         <b>sendMessage</b> - Отправляет письмо с заданными параметрами (адресат, тема, текст).
 *     </li>
 * </ul>
 */

package ru.mtuci.MindScape.auth_reg.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Value("${email-templates.user.registration.subject}")
    private String userRegistrationSubject;

    @Value("${email-templates.user.registration.text}")
    private String userRegistrationText;

    @Value("${email-templates.expert.registration.subject}")
    private String expertRegistrationSubject;

    @Value("${email-templates.expert.registration.text}")
    private String expertRegistrationText;

    @Value("${email-templates.pass-recover.subject}")
    private String passRecoverSubject;

    @Value("${email-templates.pass-recover.text}")
    private String passRecoverText;

    @Autowired
    private JavaMailSender emailSender;

    @Transactional
    public void sendCodeEmail(String to, String code, String role) {
        String subject = "";
        String text = switch (role) {
            case "user" -> {
                subject = userRegistrationSubject;
                yield String.format(userRegistrationText, code);
            }
            case "expert" -> {
                subject = expertRegistrationSubject;
                yield String.format(expertRegistrationText, code);
            }
            case "recover" -> {
                subject = passRecoverSubject;
                yield String.format(passRecoverText, code);
            }
            default -> "";
        };
        sendMessage(to, subject, text);
    }

    public void sendMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
    }
}
