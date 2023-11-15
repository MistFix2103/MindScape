/**
 * <p>Описание:</p>
 * Сервисный класс для отправки электронных писем. Поддерживает отправку писем с различными шаблонами в зависимости от типа операции.
*
 * <p>Список методов:</p>
 * <ul>
 *     <li><b>sendCodeEmail</b> - Отправляет письмо с кодом подтверждения в зависимости от желаемой операции.</li>
 *     <li><b>sendMessage</b> - Отправляет письмо с заданными параметрами (адресат, тема, текст).</li>
 * </ul>
 */

package ru.mtuci.MindScape.auth.service;

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

    @Value("${email-templates.new-mail.subject}")
    private String newMailSubject;

    @Value("${email-templates.new-mail.text}")
    private String newMailText;

    @Value("${email-templates.pass-change.subject}")
    private String passChangeSubject;

    @Value("${email-templates.pass-change.text}")
    private String passChangeText;

    @Value("${email-templates.two-step.subject}")
    private String twoStepSubject;

    @Value("${email-templates.two-step.text}")
    private String twoStepText;

    @Autowired
    private JavaMailSender emailSender;

    @Transactional
    public void sendCodeEmail(String to, String code, String type) {
        String subject;
        String text = switch (type) {
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
            case "mail_change" -> {
                subject = newMailSubject;
                yield String.format(newMailText, code);
            }
            case "pass_change" -> {
                subject = passChangeSubject;
                yield String.format(passChangeText, code);
            }
            case "two-step" -> {
                subject = twoStepSubject;
                yield String.format(twoStepText, code);
            }
            default -> throw new IllegalStateException("Unexpected value: " + type);
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
