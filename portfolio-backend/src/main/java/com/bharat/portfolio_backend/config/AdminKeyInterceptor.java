package com.bharat.portfolio_backend.config;

import com.bharat.portfolio_backend.exception.AdminAuthorizationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * Requires a matching X-Admin-Key header on every /v1 request unless the handler is
 * annotated {@link PublicEndpoint}. Default-deny on purpose: treating every GET as
 * public would leave admin-only reads such as the contact inbox exposed.
 */
@Component
public class AdminKeyInterceptor implements HandlerInterceptor {

    public static final String HEADER = "X-Admin-Key";

    private final String adminApiKey;

    public AdminKeyInterceptor(@Value("${admin.api-key:}") String adminApiKey) {
        this.adminApiKey = adminApiKey;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (isPublic(handler)) {
            return true;
        }
        if (adminApiKey.isBlank()) {
            // Fail closed: an unset key must never be read as "no auth required".
            throw new AdminAuthorizationException(HttpStatus.SERVICE_UNAVAILABLE,
                    "admin.api-key is not configured on the server");
        }
        String provided = request.getHeader(HEADER);
        if (provided == null || !constantTimeEquals(provided, adminApiKey)) {
            throw new AdminAuthorizationException(HttpStatus.UNAUTHORIZED,
                    "Invalid or missing " + HEADER + " header");
        }
        return true;
    }

    private boolean isPublic(Object handler) {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return false;
        }
        return AnnotatedElementUtils.hasAnnotation(handlerMethod.getMethod(), PublicEndpoint.class)
                || AnnotatedElementUtils.hasAnnotation(handlerMethod.getBeanType(), PublicEndpoint.class);
    }

    /**
     * Compares in constant time so response timing cannot be used to guess the key.
     */
    private boolean constantTimeEquals(String provided, String expected) {
        return MessageDigest.isEqual(
                provided.getBytes(StandardCharsets.UTF_8),
                expected.getBytes(StandardCharsets.UTF_8));
    }
}