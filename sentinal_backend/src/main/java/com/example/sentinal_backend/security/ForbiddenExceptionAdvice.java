package com.example.sentinal_backend.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestControllerAdvice
public class ForbiddenExceptionAdvice {

    private final ForbiddenAuditService forbiddenAuditService;

    public ForbiddenExceptionAdvice(ForbiddenAuditService forbiddenAuditService) {
        this.forbiddenAuditService = forbiddenAuditService;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        forbiddenAuditService.auditForbidden(request, SecurityContextHolder.getContext().getAuthentication(), ex.getMessage());
        return buildForbiddenResponse(request);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatus(ResponseStatusException ex, HttpServletRequest request) {
        if (ex.getStatusCode() != HttpStatus.FORBIDDEN) {
            throw ex;
        }

        forbiddenAuditService.auditForbidden(request, SecurityContextHolder.getContext().getAuthentication(), ex.getReason());
        return buildForbiddenResponse(request);
    }

    private ResponseEntity<Map<String, Object>> buildForbiddenResponse(HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                "status", 403,
                "error", "Forbidden",
                "message", "You do not have permission to access this resource.",
                "path", request.getRequestURI()
        ));
    }
}
