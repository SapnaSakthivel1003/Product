package com.cmms.production.user_context;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
@Component
public class UserContextInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String userIdStr = request.getHeader("X-User-Id");
        String username = request.getHeader("X-User-Name");
        String rolesStr =request.getHeader("X-User-Role");
        System.out.println("=== INTERCEPTOR TRIGGERED ===");
        System.out.println("Received X-User-Id Header: " + userIdStr);
        System.out.println("Received X-User-Name Header: " + username);
        System.out.println("Received X-User-Roles: " + rolesStr);

        if (userIdStr != null && !userIdStr.trim().isEmpty()) {
            try {
                Long userId = Long.parseLong(userIdStr.trim());
                UserContext context = new UserContext();
                context.setUserId(userId);
                context.setUsername(username);
                context.setRoles(rolesStr);
                UserContextHolder.setContext(context);

                if (handler instanceof HandlerMethod) {
                    HandlerMethod handlerMethod = (HandlerMethod) handler;

                    RequireRole requireRole = handlerMethod.getMethodAnnotation(RequireRole.class);

                    if (requireRole != null) {
                        String[] requiredRoles = requireRole.value();
                        String currentRoles = UserContextHolder.getContext().getRoles();

                        boolean hasAccess = false;
                        if (currentRoles != null) {
                            for (String role : requiredRoles) {
                                if (currentRoles.contains(role)) {
                                    hasAccess = true;
                                    break;
                                }
                            }
                        }
                        if (!hasAccess) {
                            String rolesAllowed = java.util.Arrays.toString(requiredRoles);

                            System.out.println("Custom security Blocked request! Missing one of roles: " + rolesAllowed);
                            response.setStatus(HttpStatus.FORBIDDEN.value());
                            response.getWriter().write("Access Denied: Required role from " + rolesAllowed);
                            return false;
                        }
                    }
                }
                System.out.println("user_context successfully bound to ThreadLocal for ID: " + userId);
            } catch (NumberFormatException e) {
                System.err.println("Failed to parse X-User-Id header: " + userIdStr);
            }
        } else {
            System.err.println("X-User-Id Header is completely MISSING or EMPTY!");
        }

        return true;
    }
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserContextHolder.clearContext();
    }
}



