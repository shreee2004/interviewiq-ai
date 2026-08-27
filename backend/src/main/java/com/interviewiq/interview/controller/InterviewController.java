package com.interviewiq.interview.controller;

import com.interviewiq.common.dto.PageResponse;
import com.interviewiq.interview.dto.CompanyResponse;
import com.interviewiq.interview.dto.CreateSessionRequest;
import com.interviewiq.interview.dto.JobRoleResponse;
import com.interviewiq.interview.dto.SessionDetailResponse;
import com.interviewiq.interview.dto.SessionSummaryResponse;
import com.interviewiq.interview.entity.SessionStatus;
import com.interviewiq.interview.service.InterviewService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** See docs/API_DESIGN.md §5. The WebSocket live Q&A gateway is a separate future slice (Phase 4, needs `ai`). */
@RestController
@RequestMapping("/api/v1/interviews")
public class InterviewController {

    private final InterviewService interviewService;

    public InterviewController(InterviewService interviewService) {
        this.interviewService = interviewService;
    }

    @GetMapping("/roles")
    public List<JobRoleResponse> roles() {
        return interviewService.listRoles();
    }

    @GetMapping("/companies")
    public List<CompanyResponse> companies() {
        return interviewService.listCompanies();
    }

    @PostMapping
    public ResponseEntity<SessionDetailResponse> create(
            @AuthenticationPrincipal String userId, @Valid @RequestBody CreateSessionRequest request) {
        SessionDetailResponse response = interviewService.createSession(UUID.fromString(userId), request);
        return ResponseEntity.status(201)
                .location(URI.create("/api/v1/interviews/" + response.id()))
                .body(response);
    }

    @GetMapping
    public PageResponse<SessionSummaryResponse> list(
            @AuthenticationPrincipal String userId,
            @RequestParam(required = false) SessionStatus status,
            // Without a default sort, row order across pages is undefined once a client
            // doesn't specify one — Postgres makes no ordering guarantee without ORDER BY.
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return interviewService.listSessions(UUID.fromString(userId), status, pageable);
    }

    @GetMapping("/{id}")
    public SessionDetailResponse get(@AuthenticationPrincipal String userId, @PathVariable UUID id) {
        return interviewService.getSession(UUID.fromString(userId), id);
    }

    @PostMapping("/{id}/start")
    public SessionDetailResponse start(@AuthenticationPrincipal String userId, @PathVariable UUID id) {
        return interviewService.startSession(UUID.fromString(userId), id);
    }

    @PostMapping("/{id}/abandon")
    public SessionDetailResponse abandon(@AuthenticationPrincipal String userId, @PathVariable UUID id) {
        return interviewService.abandonSession(UUID.fromString(userId), id);
    }
}
