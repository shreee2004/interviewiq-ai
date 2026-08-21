package com.interviewiq.resume.controller;

import com.interviewiq.resume.dto.ResumeResponse;
import com.interviewiq.resume.service.ResumeService;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/** See docs/API_DESIGN.md §4. Parsing and AI analysis land with the `ai` module in Phase 4. */
@RestController
@RequestMapping("/api/v1/resumes")
public class ResumeController {

    private final ResumeService resumeService;

    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    @PostMapping
    public ResponseEntity<ResumeResponse> upload(@AuthenticationPrincipal String userId, @RequestParam("file") MultipartFile file) {
        ResumeResponse response = resumeService.upload(UUID.fromString(userId), file);
        return ResponseEntity.accepted()
                .location(URI.create("/api/v1/resumes/" + response.id()))
                .body(response);
    }

    @GetMapping
    public List<ResumeResponse> list(@AuthenticationPrincipal String userId) {
        return resumeService.list(UUID.fromString(userId));
    }

    @GetMapping("/{id}")
    public ResumeResponse get(@AuthenticationPrincipal String userId, @PathVariable UUID id) {
        return resumeService.get(UUID.fromString(userId), id);
    }

    @PatchMapping("/{id}/activate")
    public ResumeResponse activate(@AuthenticationPrincipal String userId, @PathVariable UUID id) {
        return resumeService.activate(UUID.fromString(userId), id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal String userId, @PathVariable UUID id) {
        resumeService.delete(UUID.fromString(userId), id);
        return ResponseEntity.noContent().build();
    }
}
