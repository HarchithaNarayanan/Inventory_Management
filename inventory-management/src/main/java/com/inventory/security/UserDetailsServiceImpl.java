package com.inventory.security;

import com.inventory.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * UserDetailsServiceImpl — Bridges Spring Security's authentication system
 * with our MySQL {@code users} table.
 *
 * <p>Spring Security requires an implementation of {@link UserDetailsService}
 * so it knows where to look up users during authentication. This class provides
 * that implementation by querying the database via {@link UserRepository}.</p>
 *
 * <p>This service is called in two scenarios:</p>
 * <ol>
 *   <li>During JWT filter processing ({@link JwtAuthFilter}) — to reload the
 *       full user object from the database and verify the token is still valid
 *       (e.g., user hasn't been deactivated since the token was issued).</li>
 *   <li>During initial login authentication — Spring Security's
 *       {@code AuthenticationManager} calls this before password verification.</li>
 * </ol>
 *
 * <p>{@code @Service} registers this class as a Spring bean.
 * Spring Security's auto-configuration detects this bean and automatically
 * uses it for authentication — no explicit wiring needed in most cases.</p>
 *
 * <p>{@code implements UserDetailsService} — this interface has exactly one method:
 * {@link #loadUserByUsername(String)}. By implementing it, our class becomes
 * the official "user lookup" service for Spring Security.</p>
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    /**
     * Repository for querying the users table.
     * {@code @Autowired} injects the Spring Data JPA proxy at runtime.
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * Loads a user from the database by their username.
     *
     * <p>Called by Spring Security's authentication machinery.
     * The returned {@link UserDetails} contains:
     * <ul>
     *   <li>username — principal identifier</li>
     *   <li>password (BCrypt hash) — Spring Security calls {@code matches()} on this</li>
     *   <li>authorities — roles (ROLE_ADMIN / ROLE_USER)</li>
     *   <li>account status flags — isEnabled, isAccountNonLocked, etc.</li>
     * </ul>
     * </p>
     *
     * <p>{@code @Transactional} ensures that if Hibernate needs to lazily fetch
     * any associations on the User entity during this method, the session is
     * still open. Without this, lazy-loading outside a transaction would throw
     * {@code LazyInitializationException}.</p>
     *
     * @param username the username to look up (from JWT subject claim or login form)
     * @return the User entity which implements {@link UserDetails}
     * @throws UsernameNotFoundException if no user with this username exists in the DB.
     *         Spring Security catches this and returns 401 Unauthorized to the client.
     */
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Query the database for the user
        // orElseThrow converts Optional.empty() to an exception
        // UsernameNotFoundException is Spring Security's standard exception for "user not found"
        return userRepository.findByEmailId(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "User not found with username: " + username
                        )
                );
        // Note: Our User entity implements UserDetails, so we can return it directly.
        // Spring Security will call getPassword(), getAuthorities(), isEnabled(), etc.
    }
}
