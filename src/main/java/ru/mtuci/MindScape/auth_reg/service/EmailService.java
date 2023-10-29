package ru.mtuci.MindScape.auth_reg.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import ru.mtuci.MindScape.user.repository.ConfirmationCodeRepository;

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

    @Value("${email-templates.researcher.registration.subject}")
    private String researcherRegistrationSubject;

    @Value("${email-templates.researcher.registration.text}")
    private String researcherRegistrationText;

    @Value("${email-templates.pass-recover.subject}")
    private String passRecoverSubject;

    @Value("${email-templates.pass-recover.text}")
    private String passRecoverText;

    @Autowired
    private JavaMailSender emailSender;

    @Transactional
    public void sendCodeEmail(String to, String code, int role) {
        String subject = "";
        String text = switch (role) {
            case 1 -> {
                subject = userRegistrationSubject;
                yield String.format(userRegistrationText, code);
            }
            case 2 -> {
                subject = expertRegistrationSubject;
                yield String.format(expertRegistrationText, code);
            }
            case 3 -> {
                subject = researcherRegistrationSubject;
                yield String.format(researcherRegistrationText, code);
            }
            case 4 -> {
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
