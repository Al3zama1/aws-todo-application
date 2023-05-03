package com.abranlezama.awstodoapplication.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
public class CognitoOidcLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {

    private final String logoutUrl;
    private final String clientId;

    @Override
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) {
        UriComponents baseUrl = UriComponentsBuilder
                .fromHttpUrl(UrlUtils.buildRequestUrl(request))
                .replacePath(request.getContextPath())
                .replaceQuery(null)
                .fragment(null)
                .build();

        return UriComponentsBuilder
                .fromUri(URI.create(logoutUrl))
                .queryParam("client_id", clientId)
                /*
                 Url cognito will redirect users after logout.
                 It has to be valid url that was configured as part of the LogoutURs of the app client
                 */
                .queryParam("logout_uri", baseUrl)
                .encode(StandardCharsets.UTF_8)
                .build()
                .toUriString();
    }
}
