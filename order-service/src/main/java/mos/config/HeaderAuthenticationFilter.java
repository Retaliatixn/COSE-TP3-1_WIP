package mos.config;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class HeaderAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String userId = request.getHeader("X-User-Id");
        String username = request.getHeader("X-User-Name");
        String rolesHeader = request.getHeader("X-User-Roles");
        
        if (StringUtils.hasText(userId) && StringUtils.hasText(username)) {
            // Parse roles from header (format: "admin,customer,staff")
            java.util.Collection<GrantedAuthority> authorities = parseRoles(rolesHeader);
            
            // Create authentication token
            UsernamePasswordAuthenticationToken authentication = 
                new UsernamePasswordAuthenticationToken(username, null, authorities);
            
            // Set in security context
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        
        filterChain.doFilter(request, response);
    }

    private java.util.Collection<GrantedAuthority> parseRoles(String rolesHeader) {
        java.util.Collection<GrantedAuthority> authorities = new java.util.ArrayList<>();
        if (StringUtils.hasText(rolesHeader)) {
            String[] roles = rolesHeader.split(",");
            for (String role : roles) {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + role.trim().toUpperCase()));
            }
        }
        return authorities;
    }
}
