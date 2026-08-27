package com.interviewiq.admin.dto;

import jakarta.validation.constraints.Size;

/** PATCH semantics: a null field means "leave unchanged," not "clear this field." */
public record UpdateFeatureFlagRequest(Boolean enabled, @Size(max = 500) String description) {}
