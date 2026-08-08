package com.interviewiq.config;

import com.interviewiq.common.exception.ProblemDetailFactory;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

/**
 * Without this, Spring Security's default entry point (Http403ForbiddenEntryPoint, since
 * this app has no formLogin/httpBasic) returns 403 for missing/invalid credentials too —
 * docs/API_DESIGN.md §1 requires 401 for unauthenticated vs 403 for unauthorized.
 */
@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public RestAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        objectMapper.writeValue(
                response.getOutputStream(),
                ProblemDetailFactory.create(
                        HttpStatus.UNAUTHORIZED, "UNAUTHENTICATED", "Authentication required", request.getRequestURI()));
    }
}
