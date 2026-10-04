package com.dh.demo.service.impl;

import com.dh.demo.dto.RegistrationEmailDTO;
import com.dh.demo.dto.request.EmailRequestDTO;
import com.dh.demo.exception.EmailSendingException;
import com.dh.demo.service.IEmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService implements IEmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    @Async
    public void sendSimpleEmail(EmailRequestDTO emailRequest) {

        try {

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(emailRequest.getTo());
            message.setSubject(emailRequest.getSubject());
            message.setText(emailRequest.getBody());

            mailSender.send(message);
            log.info("Correo enviado exitosamente a {}", emailRequest.getTo());

        } catch (Exception ex) {
            log.error("Error al enviar correo a {}", emailRequest.getTo(), ex);
            throw new EmailSendingException("No se pudo enviar el correo a " + emailRequest.getTo());
        }
    }

    @Override
    @Async
    public void sendHtmlEmail(EmailRequestDTO emailRequest) {

        try {

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(emailRequest.getTo());
            helper.setSubject(emailRequest.getSubject());
            helper.setText(emailRequest.getBody(), true); // true = contenido HTML

            mailSender.send(mimeMessage);
            log.info("Correo HTML enviado exitosamente a {}", emailRequest.getTo());

        } catch (MessagingException ex) {
            log.error("Error al enviar correo HTML a {}", emailRequest.getTo(), ex);
            throw new EmailSendingException("No se pudo enviar el correo a " + emailRequest.getTo());
        }
    }

    @Override
    @Async
    public void sendBookingConfirmation(String to, String guestName, String hotelName, String checkIn, String checkOut) {

        String subject = "Confirmación de reserva - " + hotelName;
        String body = String.format("""
                <h2>¡Hola %s!</h2>
                <p>Tu reserva en <strong>%s</strong> ha sido confirmada.</p>
                <ul>
                    <li>Check-in: %s</li>
                    <li>Check-out: %s</li>
                </ul>
                <p>¡Gracias por elegirnos!</p>
                """, guestName, hotelName, checkIn, checkOut);

        EmailRequestDTO emailRequest = EmailRequestDTO.builder()
                .to(to)
                .subject(subject)
                .body(body)
                .build();

        sendHtmlEmail(emailRequest);
    }

    @Override
    @Async
    public void sendRegistrationConfirmation(RegistrationEmailDTO data) {

        String subject = "¡Bienvenido a SweetNest! Confirma tu registro";

        String body = String.format("""
            <h2>¡Hola %s!</h2>
            <p>Tu cuenta ha sido creada exitosamente en SweetNest.</p>
            <p><strong>Usuario:</strong> %s</p>
            <p><strong>Correo registrado:</strong> %s</p>
            <p>Si estos datos son correctos, ya puedes iniciar sesión con tu cuenta:</p>
            <p><a href="%s">Iniciar sesión</a></p>
            <p>Si no reconoces este registro, ignora este correo.</p>
            """, data.getUsername(), data.getUsername(), data.getTo(), data.getLoginUrl());

        EmailRequestDTO emailRequest = EmailRequestDTO.builder()
                .to(data.getTo())
                .subject(subject)
                .body(body)
                .build();

        sendHtmlEmail(emailRequest);
    }
}