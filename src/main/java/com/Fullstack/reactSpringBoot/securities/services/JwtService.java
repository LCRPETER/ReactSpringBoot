package com.Fullstack.reactSpringBoot.securities.services;

import com.Fullstack.reactSpringBoot.models.userManagement.Users;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@Service
public class JwtService {

    private final CustomUserDetailsService userDetailsService;

    @Value("${jwt.secret}")
    private String secretKey;

    @Autowired
    public JwtService(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    public Map<String, String> generate(String matriculeString, String role) {
        CustomUserDetails customUserDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(matriculeString);
        return generateJwt(customUserDetails.getUser());
    }

    public String extractMatricule(String token) {
        return getClaim(token, Claims::getSubject);
    }


    public boolean isTokenExpired(String token) {
        Date expirationDate = getExpirationDateFromToken(token);
        return expirationDate.before(new Date());
    }

    private Date getExpirationDateFromToken(String token) {
        return getClaim(token, Claims::getExpiration);
    }

    private <T> T getClaim(String token, Function<Claims, T> function) {
        Claims claims = getAllClaims(token);
        return function.apply(claims);
    }

    private Claims getAllClaims(String token) {
        Key key = getKey();
        log.info("Verification key: {}", Base64.getEncoder().encodeToString(key.getEncoded()));
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Map<String, String> generateJwt(Users user) {
        final long currentTime = System.currentTimeMillis();
        final long expirationTime = currentTime + 30 * 60 * 1000; // 30 minutes

        final Map<String, Object> claims = new HashMap<>();
        claims.put("nom", user.getLastName());
        claims.put(Claims.EXPIRATION, new Date(expirationTime));
        claims.put(Claims.SUBJECT, String.valueOf(user.getMatricule()));

        Key key = getKey();
        log.info("Signing key: {}", Base64.getEncoder().encodeToString(key.getEncoded()));
        final String bearer = Jwts.builder()
                .setIssuedAt(new Date(currentTime))
                .setExpiration(new Date(expirationTime))
                .setSubject(String.valueOf(user.getMatricule()))
                .addClaims(claims)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return Map.of("bearer", bearer);
    }


    private Key getKey() {
        byte[] decodedKey = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(decodedKey);
    }
}
