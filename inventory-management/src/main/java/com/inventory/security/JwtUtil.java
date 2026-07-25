package com.inventory.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JwtUtil — Utility class for all JWT (JSON Web Token) operations.
 *
 * <p>Responsibilities:</p>
 * <ul>
 *   <li>Generate signed JWT tokens on login/register</li>
 *   <li>Extract claims (username, role, expiry) from tokens</li>
 *   <li>Validate tokens — check signature + expiry + username match</li>
 * </ul>
 *
 * <p>Token structure (Base64-decoded payload example):</p>
 * <pre>
 * {
 *   "sub": "admin",              ← subject = username
 *   "role": "ROLE_ADMIN",        ← custom claim
 *   "iat": 1750505600,           ← issued at (Unix timestamp)
 *   "exp": 1750591200            ← expires at (iat + 24h)
 * }
 * </pre>
 *
 * <p>Algorithm: HMAC-SHA256 (HS256) — symmetric signing.
 * Both signing and verification use the same secret key stored in
 * {@code application.properties}.</p>
 *
 * <p>{@code @Component} makes this a Spring-managed bean so it can be
 * {@code @Autowired} into {@link JwtAuthFilter} and {@link com.inventory.service.AuthService}.</p>
 */
@Component
public class JwtUtil {

    /**
     * The JWT secret key, injected from {@code application.properties}.
     * {@code @Value("${app.jwt.secret}")} reads the property at startup.
     * The key is Base64-encoded in the properties file for safe storage.
     */
    @Value("${app.jwt.secret}")
    private String jwtSecret;

    /**
     * Token expiration in milliseconds, injected from properties.
     * Default: 86400000 ms = 24 hours.
     */
    @Value("${app.jwt.expiration}")
    private long jwtExpiration;

    // ================================================================
    // TOKEN GENERATION
    // ================================================================

    /**
     * Generates a JWT token for the given user.
     *
     * <p>The token embeds:</p>
     * <ul>
     *   <li>Subject — the username (used to reload user from DB on each request)</li>
     *   <li>Role claim — "ROLE_ADMIN" or "ROLE_USER" (for authorization)</li>
     *   <li>Issued At — current timestamp</li>
     *   <li>Expiry — current time + {@code jwtExpiration} ms</li>
     * </ul>
     *
     * @param userDetails the Spring Security user object (our User entity)
     * @return signed JWT token string (e.g., "eyJhbGciOiJIUzI1NiJ9...")
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> extraClaims = new HashMap<>();

        // Embed role in the token so the frontend can show/hide UI elements
        // based on role without another API call.
        // getAuthorities() returns a collection; we take the first (and only) role.
        if (!userDetails.getAuthorities().isEmpty()) {
            extraClaims.put("role",
                    userDetails.getAuthorities().iterator().next().getAuthority());
        }

        return buildToken(extraClaims, userDetails);
    }

    /**
     * Internal token builder using the JJWT 0.12.x fluent API.
     *
     * <p>JJWT 0.12.x changes from earlier versions:</p>
     * <ul>
     *   <li>{@code .subject()} replaces deprecated {@code .setSubject()}</li>
     *   <li>{@code .issuedAt()} replaces deprecated {@code .setIssuedAt()}</li>
     *   <li>{@code .expiration()} replaces deprecated {@code .setExpiration()}</li>
     *   <li>{@code .signWith(key)} replaces the old {@code .signWith(key, algorithm)}</li>
     * </ul>
     *
     * @param extraClaims additional claims to embed in the payload
     * @param userDetails the user whose username becomes the subject
     * @return compact JWT string
     */
    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .claims(extraClaims)                        // embed role and any extra data
                .subject(userDetails.getUsername())          // "sub" claim = username
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSigningKey())                   // sign with HMAC-SHA256
                .compact();                                  // serialize to Base64 string
    }

    // ================================================================
    // TOKEN VALIDATION
    // ================================================================

    /**
     * Validates a token by checking three conditions:
     * <ol>
     *   <li>Username in token matches the loaded UserDetails username</li>
     *   <li>Token signature is valid (not tampered with)</li>
     *   <li>Token has not expired</li>
     * </ol>
     *
     * <p>If the signature is invalid, JJWT throws a {@code JwtException}
     * which is caught in {@link JwtAuthFilter} — the request is rejected.</p>
     *
     * @param token       the JWT string from the Authorization header
     * @param userDetails the user loaded from the database
     * @return true if all three conditions pass
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        // Username match + not expired = valid token
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    /**
     * Checks if the token's expiry claim is before the current time.
     *
     * @param token the JWT string
     * @return true if the token has expired
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // ================================================================
    // CLAIM EXTRACTION
    // ================================================================

    /**
     * Extracts the username (subject claim) from the token.
     * Called by {@link JwtAuthFilter} to determine which user is making the request.
     *
     * @param token the JWT string
     * @return the username stored in the "sub" claim
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts the expiration date from the token.
     *
     * @param token the JWT string
     * @return the expiry {@link Date}
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Returns the configured token lifetime in milliseconds.
     * Used by {@link com.inventory.service.AuthService} to populate
     * {@code expiresIn} in the response DTO.
     *
     * @return jwt expiration in milliseconds
     */
    public long getJwtExpiration() {
        return jwtExpiration;
    }

    /**
     * Generic claim extractor using a {@link Function} resolver.
     *
     * <p>This pattern (passing a function) lets us extract ANY claim
     * from the token with a single method, avoiding code duplication.
     * Examples:</p>
     * <ul>
     *   <li>{@code extractClaim(token, Claims::getSubject)} → username</li>
     *   <li>{@code extractClaim(token, Claims::getExpiration)} → expiry Date</li>
     *   <li>{@code extractClaim(token, c -> c.get("role", String.class))} → role</li>
     * </ul>
     *
     * @param token          the JWT string
     * @param claimsResolver a function that extracts the desired field from Claims
     * @param <T>            the type of the extracted claim value
     * @return the extracted claim value
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Parses the token and returns all claims from the payload.
     *
     * <p>JJWT 0.12.x parsing API:</p>
     * <ul>
     *   <li>{@code Jwts.parser()} — creates a new parser builder</li>
     *   <li>{@code .verifyWith(key)} — sets the key to verify the signature against</li>
     *   <li>{@code .build()} — builds the immutable parser</li>
     *   <li>{@code .parseSignedClaims(token)} — replaces old {@code parseClaimsJws()}</li>
     *   <li>{@code .getPayload()} — returns the {@link Claims} object</li>
     * </ul>
     *
     * <p>If the token is tampered with or expired, JJWT throws a
     * {@code JwtException} subclass automatically.</p>
     *
     * @param token the JWT string to parse
     * @return all claims from the token payload
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())        // set the verification key
                .build()
                .parseSignedClaims(token)           // parse and verify signature
                .getPayload();                      // return the claims map
    }

    /**
     * Builds the cryptographic {@link SecretKey} from the Base64-encoded secret string.
     *
     * <p>{@code Keys.hmacShaKeyFor(bytes)} creates an HMAC key from raw bytes
     * and automatically validates that the key length meets HS256's 256-bit minimum.
     * If the key is too short, JJWT throws a {@code WeakKeyException} at startup.</p>
     *
     * @return the {@link SecretKey} used for both signing and verification
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
