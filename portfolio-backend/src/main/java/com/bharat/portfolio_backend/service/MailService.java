package com.bharat.portfolio_backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    private static final Logger log = LoggerFactory.getLogger(MailService.class);

    private final JavaMailSender javaMailSender;
    private final String from;
    private final String notificationTo;

    public MailService(JavaMailSender javaMailSender,
                       @Value("${spring.mail.username:}") String from,
                       @Value("${app.mail.notification-to:}") String notificationTo) {
        this.javaMailSender = javaMailSender;
        this.from = from;
        this.notificationTo = notificationTo;
    }

    public boolean isConfigured() {
        return !from.isBlank() && !notificationTo.isBlank();
    }

    /**
     * Sends to the configured inbox. Callers must persist the source record first,
     * because a delivery failure is not treated as a reason to discard it.
     */
    public void sendMail(MailRequest request) {
        if (request.to() == null || request.to().isBlank()) {
            throw new IllegalArgumentException("Recipient must be resolved from server configuration");
        }
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(request.to());
        message.setSubject(sanitizeHeader(request.subject()));
        message.setText(request.body());
        javaMailSender.send(message);
        log.info("Mail sent to {}", request.to());
    }

    /**
     * Subject values include visitor-supplied text, so strip CR/LF to stop header
     * injection (for example a subject containing "\r\nBcc: attacker@example.com").
     */
    static String sanitizeHeader(String value) {
        if (value == null) {
            return "";
        }
        return value.replaceAll("[\\r\\n]+", " ").trim();
    }

    public String notificationRecipient() {
        return notificationTo;
    }
}