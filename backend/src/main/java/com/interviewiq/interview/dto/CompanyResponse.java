package com.interviewiq.interview.dto;

import com.interviewiq.interview.entity.Company;
import java.util.UUID;

public record CompanyResponse(UUID id, String slug, String name, String logoUrl) {

    public static CompanyResponse from(Company company) {
        return new CompanyResponse(company.getId(), company.getSlug(), company.getName(), company.getLogoUrl());
    }
}
