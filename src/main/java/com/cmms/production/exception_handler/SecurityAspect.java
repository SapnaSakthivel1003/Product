package com.cmms.production.exception_handler;


import com.cmms.production.user_context.RequireRole;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
@Aspect
@Component
@RequiredArgsConstructor
public class SecurityAspect {
    @Before("within(@org.springframework.web.bind.annotation.RestController *) && @annotation(requireRole)")
    public void validateRole(RequireRole requireRole) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return;
        }

        HttpServletRequest request = attributes.getRequest();
        String userRole = request.getHeader("X-User-Role");
        if (userRole == null) {
            userRole = request.getHeader("x-user-role");
        }

        String[] allowedRoles = requireRole.value();

        if (userRole != null) {
            final String incomingRole = userRole;
            boolean hasPermission = Arrays.stream(allowedRoles).anyMatch(expectedRole ->
                    incomingRole.equalsIgnoreCase(expectedRole) ||
                            incomingRole.equalsIgnoreCase("ROLE_" + expectedRole) ||
                            ("ROLE_" + incomingRole).equalsIgnoreCase(expectedRole)
            );

            if (hasPermission) {
                return;
            }
        }
        String expectedRolesString = Arrays.toString(allowedRoles);
        throw new RoleAuthorizationException("Access Denied: Required role from " + expectedRolesString);
    }
}

