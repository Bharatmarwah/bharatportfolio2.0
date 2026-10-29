package com.bharat.portfolio_backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Forwards each new contact message to the configured inbox. Runs after commit and
 * off the request thread, so the visitor is not made to wait on SMTP and a delivery
 * failure can never discard an already-saved message.
 */
@Component
public class ContactMessageNotificationListener {

    private static final Logger log = LoggerFactory.getLogger(ContactMessageNotificationListener.class);

    private final MailService mailService;

    public ContactMessageNotificationListener(MailService mailService) {
        this.mailService = mailService;
    }

    @Async("contactMessageExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onContactMessageReceived(ContactMessageReceivedEvent event) {
        try {
            if (!mailService.isConfigured()) {
                log.warn("Contact message {} not emailed: set app.mail.notification-to and MAIL_USERNAME",
                        event.id());
                return;
            }
            mailService.sendMail(new MailRequest(
                    mailService.notificationRecipient(),
                    buildSubject(event),
                    buildBody(event)));
        } catch (RuntimeException ex) {
            log.error("Failed to email contact message {} from {}", event.id(), event.email(), ex);
        }
    }

    private String buildSubject(ContactMessageReceivedEvent event) {
        String subject = event.subject() == null || event.subject().isBlank()
                ? "New portfolio contact message"
                : event.subject();
        return "[" + subject + "] New message from " + event.name();
    }

    private String buildBody(ContactMessageReceivedEvent event) {
        String subject = event.subject() == null || event.subject().isBlank() ? "(no subject)" : event.subject();
        return """
                You have a new message from your portfolio contact form.

                Name:    %s
                Email:   %s
                Subject: %s

                Message:
                %s

                Reply directly to this address to respond to %s.
                """.formatted(
                event.name(),
                event.email(),
                subject,
                event.message(),
                event.name());
    }
}