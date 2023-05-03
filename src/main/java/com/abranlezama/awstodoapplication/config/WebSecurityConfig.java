package com.abranlezama.awstodoapplication.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

@Configuration
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final LogoutSuccessHandler logoutSuccessHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf()
                .ignoringRequestMatchers(
                        "/todo-updates/**",
                        "/websocket/**"
                )
                .and()
                .oauth2Login()
                .and()
                .authorizeHttpRequests()
                .requestMatchers(EndpointRequest.to(HealthEndpoint.class)).permitAll()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                .requestMatchers("/", "/register").permitAll()
                .anyRequest().authenticated()
                // TODO - add logout handler
                .and()
                .logout()
                .logoutSuccessHandler(logoutSuccessHandler);

        return httpSecurity.build();
    }
}
