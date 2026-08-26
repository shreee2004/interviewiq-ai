package com.interviewiq.interview.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Reference/taxonomy data (docs/DATABASE.md §2.2), not owned exclusively by this module —
 * {@code user_profiles.target_job_role_id} also points here — but {@code interview} is the
 * first module to need it read back out (GET /interviews/roles, docs/API_DESIGN.md §5), so
 * that's where the entity lives for now.
 */
@Entity
@Table(name = "job_roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobRole {

    @Id
    @GeneratedValue
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(nullable = false)
    private String name;

    private String category;
}
