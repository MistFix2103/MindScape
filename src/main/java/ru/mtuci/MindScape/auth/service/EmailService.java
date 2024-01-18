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

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.HashMap;
import java.util.Map;

@Service
public class EmailService {

    @Value("${email-templates.registration.title}")
    private String registrationTitle;

    @Value("${email-templates.registration.text1}")
    private String registrationText1;

    @Value("${email-templates.registration.text2}")
    private String registrationText2;

    @Value("${email-templates.recover.title}")
    private String recoverTitle;

    @Value("${email-templates.recover.text1}")
    private String recoverText1;

    @Value("${email-templates.recover.text2}")
    private String recoverText2;

    @Value("${email-templates.new-mail.title}")
    private String newMailTitle;

    @Value("${email-templates.new-mail.text1}")
    private String newMailText1;

    @Value("${email-templates.new-mail.text2}")
    private String newMailText2;

    @Value("${email-templates.pass-change.title}")
    private String newPassTitle;

    @Value("${email-templates.pass-change.text1}")
    private String newPassText1;

    @Value("${email-templates.pass-change.text2}")
    private String newPassText2;

    @Value("${email-templates.two-step.title}")
    private String twoStepTitle;

    @Value("${email-templates.two-step.text1}")
    private String twoStepText1;

    @Value("${email-templates.two-step.text2}")
    private String twoStepText2;

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private SpringTemplateEngine templateEngine;

    @Transactional
    public void sendCodeEmail(String to, String code, String type) {
        String subject;
        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("code", code);
        switch (type) {
            case "user", "expert":
                subject = registrationTitle;
                templateModel.put("title", registrationTitle);
                templateModel.put("text1", registrationText1);
                templateModel.put("text2", registrationText2);
                break;
            case "recover":
                subject = recoverTitle;
                templateModel.put("title", recoverTitle);
                templateModel.put("text1", recoverText1);
                templateModel.put("text2", recoverText2);
                break;
            case "mail_change":
                subject = newMailTitle;
                templateModel.put("title", newMailTitle);
                templateModel.put("text1", newMailText1);
                templateModel.put("text2", newMailText2);
                break;
            case "pass_change":
                subject = newPassTitle;
                templateModel.put("title", newPassTitle);
                templateModel.put("text1", newPassText1);
                templateModel.put("text2", newPassText2);
                break;
            case "two-step":
                subject = twoStepTitle;
                templateModel.put("title", twoStepTitle);
                templateModel.put("text1", twoStepText1);
                templateModel.put("text2", twoStepText2);
                break;
            default:
                throw new IllegalArgumentException("Неизвестный тип кода: " + type);
        }
        sendMessage(to, subject, templateModel);
    }

    public void sendMessage(String to, String subject, Map<String, Object> templateModel) {
        Context context = new Context();
        context.setVariables(templateModel);
        String htmlBody = templateEngine.process("email-template", context);

        MimeMessage mimeMessage = emailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            emailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new IllegalStateException("Failed to send email", e);
        }
    }
}
