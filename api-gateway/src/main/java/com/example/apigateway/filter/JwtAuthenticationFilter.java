package com.example.apigateway.filter;

import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {
    
    @Value("${jwt.secret:your-secret-key-change-this-in-production}")
    private String jwtSecret;
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        
        // Skip JWT validation for auth login endpoint
        if (path.contains("/auth/login")) {
            return chain.filter(exchange);
        }
        
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        
        String token = authHeader.substring(7);
        
        try {
            if (!validateToken(token)) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
            
            Long userId = extractUserId(token);
            String username = extractUsername(token);
            List<String> roles = extractRoles(token);
            
            // Add user context headers to the request
            ServerWebExchange modifiedExchange = exchange.mutate()
                    .request(exchange.getRequest().mutate()
                            .header("X-User-Id", String.valueOf(userId))
                            .header("X-User-Name", username)
                            .header("X-User-Roles", String.join(",", roles))
                            .build())
                    .build();
            
            return chain.filter(modifiedExchange);
        } catch (Exception e) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }
    
    private boolean validateToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (SecurityException | io.jsonwebtoken.MalformedJwtException | io.jsonwebtoken.ExpiredJwtException | io.jsonwebtoken.UnsupportedJwtException | IllegalArgumentException e) {
            return false;
        }
    }
    
    private Long extractUserId(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return Long.valueOf(claims.getSubject());
    }
    
    private String extractUsername(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return (String) claims.get("username");
    }
    
    @SuppressWarnings("unchecked")
    private List<String> extractRoles(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return (List<String>) claims.get("roles");
    }
    
    @Override
    public int getOrder() {
        return -1;  // Run this filter first
    }
}
