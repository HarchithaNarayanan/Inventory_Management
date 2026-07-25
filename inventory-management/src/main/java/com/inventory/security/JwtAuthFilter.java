package com.inventory.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JwtAuthFilter — Servlet filter that validates JWT tokens on every HTTP request.
 *
 * <p>This filter sits between the client and the controller. It reads the
 * {@code Authorization} header, validates the JWT token, and if valid,
 * populates Spring Security's {@code SecurityContextHolder} with the
 * authenticated user. This makes the user's identity available
 * throughout the request lifecycle.</p>
 *
 * <p>Why extend {@link OncePerRequestFilter}:</p>
 * <ul>
 *   <li>Guarantees this filter runs exactly once per HTTP request</li>
 *   <li>Prevents duplicate token validation in redirect scenarios</li>
 *   <li>Provides clean {@code doFilterInternal()} method signature</li>
 * </ul>
 *
 * <p>This filter is registered into the Spring Security filter chain in
 * {@link com.inventory.config.SecurityConfig} using
 * {@code .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)}.
 * It runs BEFORE Spring Security's default form-login filter.</p>
 *
 * <p>{@code @Component} registers this as a Spring bean so it can be
 * injected into {@link com.inventory.config.SecurityConfig}.</p>
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    /**
     * JWT utility for token parsing and validation.
     * {@code @Autowired} injects the Spring-managed {@link JwtUtil} bean.
     */
    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Service for loading user details from the database.
     * Spring Security needs the full {@link UserDetails} object to
     * set up the authentication context.
     */
    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    /**
     * Core filter logic — executed once per HTTP request.
     *
     * <p>The filter either authenticates the request (sets SecurityContext)
     * or does nothing (passes through), in which case Spring Security's
     * default handler returns 401 for protected routes.</p>
     *
     * <p>{@code @NonNull} tells IntelliJ/IDE that these parameters
     * are never null — suppresses false null-safety warnings.</p>
     *
     * @param request     the incoming HTTP request
     * @param response    the outgoing HTTP response
     * @param filterChain the chain — must call {@code filterChain.doFilter()} to continue
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // Step 1: Read the Authorization header
        // Expected format: "Bearer eyJhbGciOiJIUzI1NiJ9..."
        final String authHeader = request.getHeader("Authorization");

        // Step 2: Skip if no Authorization header or not a Bearer token
        // This lets public endpoints (login, register) pass through without token
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // Pass to the next filter without setting SecurityContext
            // SecurityConfig will handle 401 for protected routes
            filterChain.doFilter(request, response);
            return;
        }

        // Step 3: Extract the token string (everything after "Bearer ")
        // "Bearer " is 7 characters, so substring(7) removes the prefix
        final String jwt = authHeader.substring(7);
        final String username;

        try {
            // Step 4: Extract username from the token
            // This also validates the signature — throws JwtException if tampered
            username = jwtUtil.extractUsername(jwt);

        } catch (JwtException e) {
            // Token is malformed, expired, or has an invalid signature
            // Don't set SecurityContext — Spring Security will return 401
            logger.warn("JWT token validation failed: " + e.getMessage());
            filterChain.doFilter(request, response);
            return;
        }

        // Step 5: Only process if we have a username AND
        // the SecurityContext is not already authenticated
        // (avoid re-processing if already authenticated in this request)
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Step 6: Load user details from the database
            // This gives us the full User entity (with roles, active status, etc.)
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // Step 7: Validate the full token (signature + expiry + username match)
            if (jwtUtil.isTokenValid(jwt, userDetails)) {

                // Step 8: Create Spring Security authentication token
                // UsernamePasswordAuthenticationToken is Spring Security's standard
                // authentication object for username+password auth.
                //
                // Constructor args:
                //   principal   → UserDetails (the authenticated user)
                //   credentials → null (we don't need the password at this stage)
                //   authorities → the user's roles (ROLE_ADMIN, ROLE_USER)
                //
                // The 3-arg constructor marks this token as "authenticated = true"
                // The 2-arg constructor (without authorities) marks as "unauthenticated"
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,                        // credentials — not needed after JWT validation
                                userDetails.getAuthorities() // roles from UserDetails
                        );

                // Step 9: Enrich the auth token with request-specific details
                // (remote address, session ID) — useful for auditing
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // Step 10: Set the authentication into the SecurityContext
                // From this point on, the rest of the filter chain and controller
                // can access the authenticated user via:
                // SecurityContextHolder.getContext().getAuthentication()
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Step 11: Continue the filter chain
        // This passes control to the next filter and eventually the controller
        filterChain.doFilter(request, response);
    }
}
