package ru.mtuci.MindScape.auth.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
class EmailServiceTest_long {

    @Mock
    private JavaMailSender emailSender;

    @Autowired
    @InjectMocks
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void sendCodeEmail_UserType_CorrectParametersPassed() {
        String to = "test@example.com";
        String code = "123456";
        String type = "user";

        emailService.sendCodeEmail(to, code, type);

        verify(emailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendCodeEmail_ExpertType_CorrectParametersPassed() {
        String to = "test@example.com";
        String code = "123456";
        String type = "expert";

        emailService.sendCodeEmail(to, code, type);

        verify(emailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendMessage_CorrectParametersPassed() {
        String to = "test@example.com";
        String subject = "Test Subject";
        Map<String, Object> text = Map.of("key", "Test Message"); // Замени это на реальные значения

        emailService.sendMessage(to, subject, text);

        verify(emailSender, times(1)).send(any(SimpleMailMessage.class));
    }
}
