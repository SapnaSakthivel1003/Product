package com.CMMS.Production.UserContext;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class UserContextInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String userIdStr = request.getHeader("X-User-Id");
        String username = request.getHeader("X-User-Name");

        // DEBUG LOGS - Check your terminal console for these lines
        System.out.println("=== INTERCEPTOR TRIGGERED ===");
        System.out.println("Received X-User-Id Header: " + userIdStr);
        System.out.println("Received X-User-Name Header: " + username);

        if (userIdStr != null && !userIdStr.trim().isEmpty()) {
            try {
                Long userId = Long.parseLong(userIdStr.trim());
                UserContext context = new UserContext();
                context.setUserId(userId);
                context.setUsername(username);

                UserContextHolder.setContext(context);
                System.out.println("UserContext successfully bound to ThreadLocal for ID: " + userId);
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



