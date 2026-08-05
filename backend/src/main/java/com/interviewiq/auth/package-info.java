/**
 * Registration, login, JWT issuance/refresh, Google OAuth2, email verification,
 * password reset, and two-factor auth. Owns the {@code users}, {@code oauth_accounts},
 * {@code refresh_tokens}, {@code email_verification_tokens}, and
 * {@code password_reset_tokens} tables (docs/DATABASE.md §2.1).
 *
 * See docs/ARCHITECTURE.md §3 for the controller/service/repository/entity/dto/mapper
 * layering every feature package follows, and docs/API_DESIGN.md §2 for the endpoint
 * contract this package implements.
 */
package com.interviewiq.auth;
