package com.example.its.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.DefaultCsrfToken;
import org.springframework.security.web.header.HeaderWriterFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@TestConfiguration
@EnableWebSecurity
@Profile("test")
public class TestSecurityConfig {

    @Bean
    public SecurityFilterChain testFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz.anyRequest().permitAll())
            .csrf(csrf -> csrf.disable())
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())
            .sessionManagement(session -> session.disable())
            .addFilterAfter(new MockCsrfFilter(), HeaderWriterFilter.class);
        
        return http.build();
    }

    /**
     * テスト環境でCSRFトークンのモックを提供するフィルター
     */
    public static class MockCsrfFilter extends OncePerRequestFilter {
        
        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                FilterChain filterChain) throws ServletException, IOException {
            
            // モックのCSRFトークンを作成
            CsrfToken mockToken = new DefaultCsrfToken("X-CSRF-TOKEN", "_csrf", "test-csrf-token");
            
            // リクエスト属性として設定
            request.setAttribute(CsrfToken.class.getName(), mockToken);
            request.setAttribute("_csrf", mockToken);
            
            filterChain.doFilter(request, response);
        }
    }
} 