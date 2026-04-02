package com.example.sentinal_backend.security;

import io.micrometer.core.instrument.MeterRegistry;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ForbiddenAuditService {

    private final MeterRegistry meterRegistry;

    public void auditForbidden(HttpServletRequest request, Authentication authentication, String reason) {
        String user = authentication != null ? authentication.getName() : "anonymous";
        String roles = authentication != null
                ? authentication.getAuthorities().toString()
                : "[]";
        String method = request.getMethod();
        String path = request.getRequestURI();

        log.warn("SECURITY_AUDIT 403 user={} roles={} method={} path={} remote={} reason={}",
                user, roles, method, path, request.getRemoteAddr(), reason);
        meterRegistry.counter("sentinal.security.forbidden", "path", path, "method", method).increment();
    }
}
