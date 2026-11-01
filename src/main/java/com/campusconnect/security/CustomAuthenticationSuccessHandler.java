package com.campusconnect.security;

import com.campusconnect.model.Role;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if (authority.getAuthority().equals(Role.ROLE_MANAGEMENT.name())) {
                response.sendRedirect("/management/dashboard");
                return;
            } else if (authority.getAuthority().equals(Role.ROLE_STUDENT.name())) {
                response.sendRedirect("/student/dashboard");
                return;
            }
        }
        response.sendRedirect("/home");
    }
}

