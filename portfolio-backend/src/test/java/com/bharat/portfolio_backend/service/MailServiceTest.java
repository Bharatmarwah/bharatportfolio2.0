package com.bharat.portfolio_backend.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MailServiceTest {

    @Mock
    private JavaMailSender javaMailSender;

    @Test
    void stripsNewlinesFromVisitorSuppliedSubject() {
        MailService mailService = new MailService(javaMailSender, "owner@example.com", "inbox@example.com");

        mailService.sendMail(new MailRequest(
                "inbox@example.com",
                "New message\r\nBcc: attacker@example.com",
                "body"));

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(javaMailSender).send(captor.capture());
        assertThat(captor.getValue().getSubject())
                .isEqualTo("New message Bcc: attacker@example.com")
                .doesNotContain("\r", "\n");
    }

    @Test
    void isNotConfiguredWithoutFromOrRecipient() {
        assertThat(new MailService(javaMailSender, "", "inbox@example.com").isConfigured()).isFalse();
        assertThat(new MailService(javaMailSender, "owner@example.com", "").isConfigured()).isFalse();
        assertThat(new MailService(javaMailSender, "owner@example.com", "inbox@example.com").isConfigured())
                .isTrue();
    }
}
