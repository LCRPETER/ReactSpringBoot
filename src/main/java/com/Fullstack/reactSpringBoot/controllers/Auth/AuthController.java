package com.Fullstack.reactSpringBoot.controllers.Auth;

import com.Fullstack.reactSpringBoot.dto.auth.AuthDTO;
import com.Fullstack.reactSpringBoot.securities.services.JwtService;
import com.Fullstack.reactSpringBoot.securities.services.TokenBlacklistService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final TokenBlacklistService tokenBlacklistService;

    @PostMapping(path = "login")
    public ResponseEntity<Map<String, String>> auth(@RequestBody AuthDTO authDTO) {
        log.info("Connexion attempt for user: {}", authDTO.matricule());
        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authDTO.matricule(), authDTO.password())
        );

        if (authenticate.isAuthenticated()) {
            log.info("User authenticated: {}", authDTO.matricule());
            SecurityContextHolder.getContext().setAuthentication(authenticate);
            String role = authenticate.getAuthorities().stream()
                    .findFirst()
                    .map(grantedAuthority -> grantedAuthority.getAuthority())
                    .orElse("ROLE_USER");

            Map<String, String> token = this.jwtService.generate(String.valueOf(authDTO.matricule()), role);
            return ResponseEntity.ok(token);
        } else {
            log.warn("Authentication failed for user: {}", authDTO.matricule());
            throw new RuntimeException("Authentication failed");
        }
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            tokenBlacklistService.blacklistToken(token);
            return ResponseEntity.ok("Logout successful");
        }
        return ResponseEntity.badRequest().body("Invalid Authorization header");
    }
}
