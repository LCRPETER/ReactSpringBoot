package com.Fullstack.reactSpringBoot.securities.filter;

import com.Fullstack.reactSpringBoot.securities.services.CustomUserDetailsService;
import com.Fullstack.reactSpringBoot.securities.services.JwtService;
import com.Fullstack.reactSpringBoot.securities.services.TokenBlacklistService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Service
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;
    private final TokenBlacklistService tokenBlacklistService;


    @Autowired
    public JwtFilter(JwtService jwtService, CustomUserDetailsService userDetailsService, TokenBlacklistService tokenBlacklistService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.tokenBlacklistService = tokenBlacklistService;

    }

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
            String matriculeString = null;
            boolean isTokenExpired = true;

            String path = request.getRequestURI();
            if (path.startsWith("/api/auth")) {
                filterChain.doFilter(request, response);
                return;
            }

            final String authorization = request.getHeader("Authorization");
            if (authorization != null && authorization.startsWith("Bearer ")) {
                String token = authorization.substring(7);
                log.info("Received token: {}", token);

                if (tokenBlacklistService.isTokenBlacklisted(token)) {
                    log.warn("Token is blacklisted");
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }

                try {
                    isTokenExpired = jwtService.isTokenExpired(token);
                    matriculeString = jwtService.extractMatricule(token);
                } catch (Exception e) {
                    log.warn("Token validation error: {}", e.getMessage());
                }
            }

            if (!isTokenExpired && matriculeString != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(matriculeString);
                if (userDetails != null) {
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    log.info("Token is valid. User authenticated: {}", matriculeString);
                }
            } else {
                log.warn("Token is invalid or expired");
        }

        filterChain.doFilter(request, response);
    }


}

