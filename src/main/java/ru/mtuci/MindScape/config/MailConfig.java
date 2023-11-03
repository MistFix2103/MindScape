/**
 * <p>Описание:</p>
 * Конфигурационный класс для настройки JavaMailSender. Объявляет и конфигурирует бин для отправки электронных писем.
 *
 * <p>Методы:</p>
 * <ul>
 *     <li>
 *         <b>mailSender</b> - Объявляет бин типа JavaMailSender и конфигурирует его для работы с smtp.mailtrap.io.
 *     </li>
 * </ul>
 */

package ru.mtuci.MindScape.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {

    @Bean
    public JavaMailSender mailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.mailtrap.io");
        mailSender.setPort(2525);

        mailSender.setUsername("41023d10a1a9ec");
        mailSender.setPassword("d646085820fe08");

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        return mailSender;
    }
}