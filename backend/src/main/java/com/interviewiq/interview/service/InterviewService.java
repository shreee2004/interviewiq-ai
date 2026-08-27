package com.interviewiq.interview.service;

import com.interviewiq.common.dto.PageResponse;
import com.interviewiq.common.exception.BusinessRuleException;
import com.interviewiq.common.exception.ResourceNotFoundException;
import com.interviewiq.interview.dto.CompanyResponse;
import com.interviewiq.interview.dto.CreateSessionRequest;
import com.interviewiq.interview.dto.JobRoleResponse;
import com.interviewiq.interview.dto.SessionDetailResponse;
import com.interviewiq.interview.dto.SessionSummaryResponse;
import com.interviewiq.interview.entity.InterviewSession;
import com.interviewiq.interview.entity.SessionStatus;
import com.interviewiq.interview.event.SessionStartedEvent;
import com.interviewiq.interview.repository.CompanyRepository;
import com.interviewiq.interview.repository.InterviewSessionRepository;
import com.interviewiq.interview.repository.InterviewTurnRepository;
import com.interviewiq.interview.repository.JobRoleRepository;
import com.interviewiq.resume.repository.ResumeRepository;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Session lifecycle only (create/list/get/start/abandon) — the live question/answer turn
 * loop over WebSocket is a separate future slice, gated on the {@code ai} module (Phase 4)
 * that actually generates questions and evaluates answers. See package-info for the
 * full-flow sequence diagram this module works toward.
 */
@Service
@Transactional
public class InterviewService {

    private final InterviewSessionRepository sessionRepository;
    private final InterviewTurnRepository turnRepository;
    private final JobRoleRepository jobRoleRepository;
    private final CompanyRepository companyRepository;
    private final ResumeRepository resumeRepository;
    private final ApplicationEventPublisher eventPublisher;

    public InterviewService(
            InterviewSessionRepository sessionRepository,
            InterviewTurnRepository turnRepository,
            JobRoleRepository jobRoleRepository,
            CompanyRepository companyRepository,
            ResumeRepository resumeRepository,
            ApplicationEventPublisher eventPublisher) {
        this.sessionRepository = sessionRepository;
        this.turnRepository = turnRepository;
        this.jobRoleRepository = jobRoleRepository;
        this.companyRepository = companyRepository;
        this.resumeRepository = resumeRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional(readOnly = true)
    public List<JobRoleResponse> listRoles() {
        return jobRoleRepository.findAll().stream().map(JobRoleResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<CompanyResponse> listCompanies() {
        return companyRepository.findAll().stream().map(CompanyResponse::from).toList();
    }

    public SessionDetailResponse createSession(UUID userId, CreateSessionRequest request) {
        if (request.jobRoleId() != null && !jobRoleRepository.existsById(request.jobRoleId())) {
            throw ResourceNotFoundException.of("JobRole", request.jobRoleId());
        }
        if (request.companyId() != null && !companyRepository.existsById(request.companyId())) {
            throw ResourceNotFoundException.of("Company", request.companyId());
        }
        if (request.resumeId() != null && resumeRepository.findByIdAndUserId(request.resumeId(), userId).isEmpty()) {
            throw ResourceNotFoundException.of("Resume", request.resumeId());
        }

        InterviewSession session = InterviewSession.builder()
                .userId(userId)
                .resumeId(request.resumeId())
                .jobRoleId(request.jobRoleId())
                .companyId(request.companyId())
                .interviewType(request.interviewType())
                .difficulty(request.difficulty())
                .language(request.language())
                .plannedDurationMinutes(request.plannedDurationMinutes())
                .build();
        sessionRepository.save(session);
        return SessionDetailResponse.from(session, List.of());
    }

    @Transactional(readOnly = true)
    public PageResponse<SessionSummaryResponse> listSessions(UUID userId, SessionStatus status, Pageable pageable) {
        var page = status == null
                ? sessionRepository.findAllByUserId(userId, pageable)
                : sessionRepository.findAllByUserIdAndStatus(userId, status, pageable);
        return PageResponse.from(page.map(SessionSummaryResponse::from));
    }

    @Transactional(readOnly = true)
    public SessionDetailResponse getSession(UUID userId, UUID sessionId) {
        InterviewSession session = requireOwnedSession(userId, sessionId);
        return SessionDetailResponse.from(session, turnRepository.findAllBySessionIdOrderBySequenceNoAsc(sessionId));
    }

    public SessionDetailResponse startSession(UUID userId, UUID sessionId) {
        InterviewSession session = requireOwnedSession(userId, sessionId);
        if (session.getStatus() != SessionStatus.PENDING) {
            throw new BusinessRuleException("Only a PENDING session can be started");
        }
        session.setStatus(SessionStatus.IN_PROGRESS);
        session.setStartedAt(Instant.now());
        eventPublisher.publishEvent(new SessionStartedEvent(userId, sessionId, session.getStartedAt()));
        return SessionDetailResponse.from(session, turnRepository.findAllBySessionIdOrderBySequenceNoAsc(sessionId));
    }

    public SessionDetailResponse abandonSession(UUID userId, UUID sessionId) {
        InterviewSession session = requireOwnedSession(userId, sessionId);
        if (session.getStatus() != SessionStatus.PENDING && session.getStatus() != SessionStatus.IN_PROGRESS) {
            throw new BusinessRuleException("Only a PENDING or IN_PROGRESS session can be abandoned");
        }
        session.setStatus(SessionStatus.ABANDONED);
        session.setCompletedAt(Instant.now());
        return SessionDetailResponse.from(session, turnRepository.findAllBySessionIdOrderBySequenceNoAsc(sessionId));
    }

    private InterviewSession requireOwnedSession(UUID userId, UUID sessionId) {
        return sessionRepository
                .findByIdAndUserId(sessionId, userId)
                .orElseThrow(() -> ResourceNotFoundException.of("InterviewSession", sessionId));
    }
}
