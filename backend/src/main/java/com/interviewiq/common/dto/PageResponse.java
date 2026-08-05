package com.interviewiq.common.dto;

import java.util.List;
import org.springframework.data.domain.Page;

/** Pagination envelope shared by every list endpoint — see docs/API_DESIGN.md §1. */
public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages) {

    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages());
    }
}
