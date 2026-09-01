package com.interviewiq.notification.service;

import com.interviewiq.auth.event.EmailVerificationRequestedEvent;
import com.interviewiq.auth.event.UserRegisteredEvent;
import com.interviewiq.common.dto.PageResponse;
import com.interviewiq.common.exception.ResourceNotFoundException;
import com.interviewiq.config.InterviewIqProperties;
import com.interviewiq.notification.dto.NotificationResponse;
import com.interviewiq.notification.entity.Notification;
import com.interviewiq.notification.repository.NotificationRepository;
import java.time.Instant;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final JavaMailSender mailSender;
    private final InterviewIqProperties properties;

    public NotificationService(
            NotificationRepository notificationRepository, JavaMailSender mailSender, InterviewIqProperties properties) {
        this.notificationRepository = notificationRepository;
        this.mailSender = mailSender;
        this.properties = properties;
    }

    /** Same AFTER_COMMIT reasoning as {@link #onUserRegistered} — an SMTP call is external I/O that must never be able to fail registration. */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onEmailVerificationRequested(EmailVerificationRequestedEvent event) {
        String link = properties.frontendBaseUrl() + "/verify-email?token=" + event.rawToken();
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(properties.notification().mailFrom());
        message.setTo(event.email());
        message.setSubject("Verify your InterviewIQ AI account");
        message.setText("Confirm your email to activate your account:\n\n" + link
                + "\n\nIf you didn't create this account, you can ignore this email.");
        mailSender.send(message);
    }

    /**
     * AFTER_COMMIT (unlike {@code AnalyticsService#onSessionStarted}'s plain, same-transaction
     * {@code @EventListener}) — a welcome message is a best-effort side effect, not part of
     * what "registration succeeding" means. A plain listener runs inside AuthService.register's
     * transaction, so a failure here (a DB blip, a future bug) would roll registration back
     * too; firing only after that transaction commits means account creation can never fail
     * because of it. Spring requires this method's own propagation to be explicit
     * (REQUIRES_NEW/NOT_SUPPORTED) rather than inheriting the class-level @Transactional —
     * by AFTER_COMMIT there's no transaction left to join, so REQUIRED isn't a valid choice
     * here and Spring rejects it at startup.
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onUserRegistered(UserRegisteredEvent event) {
        create(
                event.userId(),
                "WELCOME",
                "Welcome to InterviewIQ AI",
                "Upload a resume and start your first practice interview whenever you're ready.");
    }

    private void create(UUID userId, String type, String title, String body) {
        notificationRepository.save(
                Notification.builder().userId(userId).type(type).title(title).body(body).build());
    }

    @Transactional(readOnly = true)
    public PageResponse<NotificationResponse> list(UUID userId, Boolean unread, Pageable pageable) {
        var page = unread == null
                ? notificationRepository.findAllByUserId(userId, pageable)
                : unread
                        ? notificationRepository.findAllByUserIdAndReadAtIsNull(userId, pageable)
                        : notificationRepository.findAllByUserIdAndReadAtIsNotNull(userId, pageable);
        return PageResponse.from(page.map(NotificationResponse::from));
    }

    public NotificationResponse markRead(UUID userId, UUID notificationId) {
        Notification notification = notificationRepository
                .findById(notificationId)
                .filter(n -> n.getUserId().equals(userId))
                .orElseThrow(() -> ResourceNotFoundException.of("Notification", notificationId));
        if (notification.getReadAt() == null) {
            notification.setReadAt(Instant.now());
        }
        return NotificationResponse.from(notification);
    }

    public void markAllRead(UUID userId) {
        notificationRepository.markAllRead(userId, Instant.now());
    }
}
