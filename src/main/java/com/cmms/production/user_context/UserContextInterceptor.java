package com.cmms.production.user_context;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import java.util.Arrays;
@Slf4j
@Component
public class UserContextInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        String userIdStr = request.getHeader("X-User-Id");

        if (userIdStr == null || userIdStr.trim().isEmpty()) {
            return true;
        }

        try {
            initUserContext(request, userIdStr);

            if (handler instanceof HandlerMethod handlerMethod) {
                return checkRoleAccess(handlerMethod, response);
            }
        } catch (NumberFormatException e) {
            log.error("Failed to parse X-User-Id header: {}", userIdStr);
        }
        return true;
    }

    private void initUserContext(HttpServletRequest request, String userIdStr) {
        Long userId = Long.parseLong(userIdStr.trim());
        String username = request.getHeader("X-User-Name");
        String rolesStr = request.getHeader("X-User-Role");

        UserContext context = new UserContext();
        context.setUserId(userId);
        context.setUsername(username);
        context.setRoles(rolesStr);
        UserContextHolder.setContext(context);
    }

    private boolean checkRoleAccess(HandlerMethod handlerMethod, HttpServletResponse response) throws Exception {
        RequireRole requireRole = handlerMethod.getMethodAnnotation(RequireRole.class);
        if (requireRole == null) {
            return true;
        }

        String[] requiredRoles = requireRole.value();
        String currentRoles = UserContextHolder.getContext().getRoles();

        if (hasMatchingRole(requiredRoles, currentRoles)) {
            return true;
        }
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.getWriter().write("Access Denied: Required role from " + Arrays.toString(requiredRoles));
        return false;
    }

    private boolean hasMatchingRole(String[] requiredRoles, String currentRoles) {
        if (currentRoles == null) {
            return false;
        }
        for (String role : requiredRoles) {
            if (currentRoles.contains(role)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void afterCompletion(@lombok.NonNull HttpServletRequest request, @lombok.NonNull HttpServletResponse response, @lombok.NonNull Object handler, Exception ex) throws Exception {
        UserContextHolder.clearContext();
    }
}





