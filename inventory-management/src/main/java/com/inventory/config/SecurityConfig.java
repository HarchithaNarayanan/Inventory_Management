package com.inventory.config;

import com.inventory.security.JwtAuthFilter;
import com.inventory.security.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * SecurityConfig — Central Spring Security configuration for the Inventory Management System.
 *
 * <p>This class replaces the old WebMvcConfigurer-based CORS config for security
 * and defines all URL-based authorization rules.</p>
 *
 * <p>Key annotations:</p>
 * <ul>
 *   <li>{@code @Configuration} — Marks this as a Spring configuration class
 *       containing {@code @Bean} method definitions</li>
 *   <li>{@code @EnableWebSecurity} — Activates Spring Security's full web security
 *       support. Without this, security rules are not applied.</li>
 *   <li>{@code @EnableMethodSecurity} — Enables {@code @PreAuthorize} annotations
 *       on individual service/controller methods for fine-grained access control</li>
 * </ul>
 *
 * <p>Architecture: This config creates a {@link SecurityFilterChain} bean that
 * defines the complete filter chain. Spring Security replaces its internal
 * default chain with ours.</p>
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    // ================================================================
    // SECURITY FILTER CHAIN — the main security ruleset
    // ================================================================

    /**
     * Defines the HTTP security rules: CORS, CSRF, authorization, session, and filter order.
     *
     * <p>This {@code @Bean} replaces Spring Security's default filter chain.</p>
     *
     * @param http the {@link HttpSecurity} builder provided by Spring
     * @return the configured {@link SecurityFilterChain}
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // --------------------------------------------------------
            // CORS — must be configured here (not just WebMvcConfigurer)
            // because Spring Security's filter runs BEFORE Spring MVC.
            // Without cors() here, Spring Security blocks cross-origin
            // requests before MVC ever sees them.
            // --------------------------------------------------------
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            // --------------------------------------------------------
            // CSRF — Disabled because we use stateless JWT, not sessions.
            // CSRF protection is only necessary when the browser
            // automatically sends credentials (cookies/sessions).
            // With JWT in Authorization headers, CSRF is not a concern.
            // AbstractHttpConfigurer::disable is the Spring Security 6 way
            // to disable a configurer.
            // --------------------------------------------------------
            .csrf(AbstractHttpConfigurer::disable)

            // --------------------------------------------------------
            // AUTHORIZATION RULES — defines which URLs require what access
            // Rules are evaluated TOP-TO-BOTTOM; first match wins.
            // --------------------------------------------------------
            .authorizeHttpRequests(auth -> auth

                // ADMIN ONLY — list all users
                .requestMatchers(HttpMethod.GET, "/api/auth/users").hasAuthority("ROLE_ADMIN")

                // PUBLIC — Login and Register are open to everyone (no token needed)
                .requestMatchers("/api/auth/**").permitAll()

                // ADMIN ONLY MODULE
                .requestMatchers("/api/banks/**")
                        .hasAuthority("ROLE_ADMIN")

                // ADMIN & MANAGER MODULES
                .requestMatchers("/api/suppliers/**", "/api/customers/**", "/api/reports/**")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_MANAGER")

                // ADMIN & STAFF MODULES
                .requestMatchers("/api/receipts/**")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_STAFF")

                // ALL ROLES (ADMIN, MANAGER, STAFF) MODULES
                .requestMatchers("/api/items/**", "/api/billing/**")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_MANAGER", "ROLE_STAFF")

                // Any other request requires authentication
                // (catch-all for future endpoints)
                .anyRequest().authenticated()
            )

            // --------------------------------------------------------
            // SESSION MANAGEMENT — Stateless (no server-side sessions)
            // JWT is stateless by design: each token is self-contained.
            // STATELESS tells Spring Security:
            //   - Never create an HttpSession
            //   - Never store SecurityContext in session
            //   - Each request must authenticate independently
            // --------------------------------------------------------
            .sessionManagement(session ->
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // --------------------------------------------------------
            // AUTHENTICATION PROVIDER — tells Spring Security how to
            // verify credentials. DaoAuthenticationProvider:
            //   1. Uses our UserDetailsService to load the user from DB
            //   2. Uses BCrypt to compare the submitted password with the hash
            // --------------------------------------------------------
            .authenticationProvider(authenticationProvider())

            // --------------------------------------------------------
            // JWT FILTER — insert our filter BEFORE Spring's default
            // UsernamePasswordAuthenticationFilter. This ensures that
            // JWT authentication is processed first on every request.
            // --------------------------------------------------------
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // ================================================================
    // CORS CONFIGURATION — Spring Security compatible
    // ================================================================

    /**
     * Configures CORS rules as a Spring Security-compatible bean.
     *
     * <p>This replaces the old {@link CorsConfig} WebMvcConfigurer approach.
     * Spring Security's filter chain needs CORS configured at the security
     * layer (not just the MVC layer) to properly allow pre-flight OPTIONS requests.</p>
     *
     * <p>Allowed origins include the React Vite dev server ({@code http://localhost:5173})
     * and the standard dev port ({@code http://localhost:3000}) for flexibility.</p>
     *
     * @return a {@link CorsConfigurationSource} used by the security filter chain
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Allow requests from React Vite dev server and standard React port
        configuration.setAllowedOrigins(List.of(
                "http://localhost:5173",   // Vite default port
                "http://localhost:3000"    // Create React App default port
        ));

        // Allow standard HTTP methods
        configuration.setAllowedMethods(List.of(
                "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"
        ));

        // Allow all headers including Authorization (needed for JWT)
        configuration.setAllowedHeaders(List.of("*"));

        // Allow cookies/auth headers to be included in CORS requests
        configuration.setAllowCredentials(true);

        // Expose Authorization header to the frontend JavaScript
        configuration.setExposedHeaders(List.of("Authorization"));

        // Apply this CORS config to all paths
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    // ================================================================
    // AUTHENTICATION PROVIDER
    // ================================================================

    /**
     * Configures how Spring Security authenticates users.
     *
     * <p>{@link DaoAuthenticationProvider} is the standard provider for
     * database-backed authentication:</p>
     * <ol>
     *   <li>Calls {@code userDetailsService.loadUserByUsername()} to get the stored user</li>
     *   <li>Calls {@code passwordEncoder.matches(rawPassword, storedHash)} to verify</li>
     *   <li>Checks account status (enabled, non-locked, etc.)</li>
     * </ol>
     *
     * @return configured {@link AuthenticationProvider}
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        // Tell the provider where to find users (our MySQL DB)
        provider.setUserDetailsService(userDetailsService);
        // Tell the provider how passwords are hashed (BCrypt)
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    // ================================================================
    // PASSWORD ENCODER
    // ================================================================

    /**
     * Creates the BCrypt password encoder bean.
     *
     * <p>BCrypt is a one-way adaptive hashing function designed for passwords:</p>
     * <ul>
     *   <li>Includes a random salt automatically — same password → different hash each time</li>
     *   <li>Work factor (cost) defaults to 10 — computationally expensive to brute-force</li>
     *   <li>One-way — the original password can never be recovered from the hash</li>
     * </ul>
     *
     * <p>Used in {@link com.inventory.service.AuthService#register} to hash passwords
     * and in {@link DaoAuthenticationProvider} to verify login passwords.</p>
     *
     * @return {@link BCryptPasswordEncoder} instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ================================================================
    // AUTHENTICATION MANAGER
    // ================================================================

    /**
     * Exposes the {@link AuthenticationManager} as a Spring bean.
     *
     * <p>The {@code AuthenticationManager} is the entry point for Spring Security's
     * authentication process. It delegates to the configured
     * {@link AuthenticationProvider} (our {@link DaoAuthenticationProvider}).</p>
     *
     * <p>It is injected into {@link com.inventory.service.AuthService}
     * and called during the login flow to trigger credential verification.</p>
     *
     * @param config auto-configured by Spring Boot
     * @return the application's {@link AuthenticationManager}
     * @throws Exception if the manager cannot be retrieved
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }
}
