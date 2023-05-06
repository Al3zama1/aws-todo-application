package com.abranlezama.awstodoapplication.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.servlet.HandlerInterceptor;

// add the name of a user to each log event
@Slf4j
public class LoggingContextInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId =getUserIdFromPrincipal(authentication.getPrincipal());
        MDC.put("userId", userId);
        return true;
    }

    private String getUserIdFromPrincipal(Object principal) {
        // anonymous users will have a String principal with value "anonymousUser"
        if (principal instanceof String) return principal.toString();

        if (principal instanceof OidcUser) {
            try {
                OidcUser user = (OidcUser) principal;
                if (user.getPreferredUsername() != null) return user.getPreferredUsername();
                else if (user.getClaimAsString("name") != null) return user.getClaimAsString("name");
                else return "unknown";
            } catch (Exception e) {
                log.warn("Could not extract userId from Principal", e);
            }
        }

        return "unknown";
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        MDC.clear(); // Message Diagnostic Context
    }
}
