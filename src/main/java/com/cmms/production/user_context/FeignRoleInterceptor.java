package com.cmms.production.user_context;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class FeignRoleInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            template.header("X-User-Id", request.getHeader("X-User-Id"));
            template.header("X-User-Name", request.getHeader("X-User-Name"));
            template.header("X-User-Role", request.getHeader("X-User-Role"));
        }
    }
}

