package com.interviewiq.auth.repository;

import com.interviewiq.auth.entity.User;
import com.interviewiq.auth.entity.UserStatus;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    // Optional filters (:status / :emailPattern may be null) — used by admin's user search
    // (docs/API_DESIGN.md §11), which is why this lives on the shared repository rather
    // than a query built ad hoc in the admin module. emailPattern is pre-lowercased and
    // wrapped in '%...%' by the caller — building it in SQL via lower(concat(...)) instead
    // made Postgres unable to infer :email's type when null (it's referenced in two
    // different expressions), failing with "function lower(bytea) does not exist".
    @Query("select u from User u "
            + "where (:status is null or u.status = :status) "
            + "and (:emailPattern is null or lower(u.email) like :emailPattern)")
    Page<User> search(@Param("status") UserStatus status, @Param("emailPattern") String emailPattern, Pageable pageable);
}
