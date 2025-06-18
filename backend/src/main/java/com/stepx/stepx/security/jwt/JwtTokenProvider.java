package com.stepx.stepx.security.jwt;

import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.stepx.stepx.model.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtTokenProvider {
    private static final Logger log = LoggerFactory.getLogger(JwtTokenProvider.class);

    private static final String ACCESS_TOKEN_COOKIE_NAME = "AccessToken";
    private static final String REFRESH_TOKEN_COOKIE_NAME = "RefreshToken";

    private static final String TOKEN_TYPE_ACCESS = "ACCESS";
    private static final String TOKEN_TYPE_REFRESH = "REFRESH";

    private static final long ACCESS_TOKEN_EXPIRATION_MINUTES = 15;
    private static final long REFRESH_TOKEN_EXPIRATION_DAYS = 7;

    private final SecretKey jwtSecret;
    private final JwtParser jwtParser;

    public JwtTokenProvider() {
        this.jwtSecret = Jwts.SIG.HS256.key().build();
        this.jwtParser = Jwts.parser().verifyWith(jwtSecret).build();
    }

    // ==================== TOKEN RESOLUTION ====================

    public String resolveToken(HttpServletRequest request, boolean fromCookie) {
        try {
            return fromCookie ? getTokenFromCookies(request) : getTokenFromHeaders(request);
        } catch (IllegalArgumentException e) {
            log.debug("Token resolution failed: {}", e.getMessage());
            return null;
        }
    }

    private String getTokenFromHeaders(HttpServletRequest request) {
        String bearer = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (!StringUtils.hasText(bearer)) {
            throw new IllegalArgumentException("Authorization header is missing");
        }
        if (!bearer.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Authorization header must start with 'Bearer '");
        }
        return bearer.substring(7);
    }

    private String getTokenFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) throw new IllegalArgumentException("No cookies found");

        for (Cookie cookie : cookies) {
            if (ACCESS_TOKEN_COOKIE_NAME.equals(cookie.getName())) {
                String value = cookie.getValue();
                if (!StringUtils.hasText(value)) {
                    throw new IllegalArgumentException("AccessToken cookie is empty");
                }
                return value;
            }
        }
        throw new IllegalArgumentException("AccessToken cookie not found");
    }

    // ==================== TOKEN VALIDATION ====================

    public boolean isTokenValid(String token) {
        try {
            parseToken(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    public Claims parseToken(String token) throws JwtException {
        try {
            return jwtParser.parseSignedClaims(token).getPayload();
        } catch (ExpiredJwtException e) {
            log.warn("Token expired: {}", e.getMessage());
            throw new JwtException("Token expired", e);
        } catch (MalformedJwtException e) {
            log.warn("Malformed token: {}", e.getMessage());
            throw new JwtException("Invalid token format", e);
        } catch (SignatureException e) {
            log.warn("Invalid signature: {}", e.getMessage());
            throw new JwtException("Invalid token signature", e);
        } catch (IllegalArgumentException e) {
            log.warn("Empty claims: {}", e.getMessage());
            throw new JwtException("Token claims are empty", e);
        } catch (Exception e) {
            log.error("Unexpected token error", e);
            throw new JwtException("Token validation error", e);
        }
    }

    public Claims validateAndParseToken(HttpServletRequest request, boolean fromCookie) {
        String token = resolveToken(request, fromCookie);
        if (token == null) throw new JwtException("Token not found");
        return parseToken(token);
    }

    // ==================== TOKEN GENERATION ====================

    public String generateAccessToken(UserDetails userDetails) {
        return buildToken(userDetails, ACCESS_TOKEN_EXPIRATION_MINUTES, TOKEN_TYPE_ACCESS).compact();
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return buildToken(userDetails, REFRESH_TOKEN_EXPIRATION_DAYS * 24 * 60, TOKEN_TYPE_REFRESH).compact();
    }

    public String refreshAccessToken(UserDetails userDetails) {
        try {
            return generateAccessToken(userDetails);
        } catch (Exception e) {
            log.error("Token refresh failed for {}: {}", userDetails.getUsername(), e.getMessage());
            throw new JwtException("Token refresh failed", e);
        }
    }

    private JwtBuilder buildToken(UserDetails userDetails, long expirationMinutes, String tokenType) {
        if (!(userDetails instanceof User user)) {
            throw new IllegalArgumentException("UserDetails must be of type User");
        }

        Instant now = Instant.now();

        return Jwts.builder()
                .claim("userId", user.getId())
                .claim("username", user.getUsername())
                .claim("roles", user.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList())
                .claim("type", tokenType)
                .subject(user.getUsername())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(TimeUnit.MINUTES.toMillis(expirationMinutes))))
                .signWith(jwtSecret);
    }

    // ==================== COOKIE MANAGEMENT ====================

    public void addTokensToResponse(HttpServletResponse response, String accessToken, String refreshToken, boolean secure) {
        response.addHeader(HttpHeaders.SET_COOKIE, buildCookie(ACCESS_TOKEN_COOKIE_NAME, accessToken, ACCESS_TOKEN_EXPIRATION_MINUTES, secure).toString());
        response.addHeader(HttpHeaders.SET_COOKIE, buildCookie(REFRESH_TOKEN_COOKIE_NAME, refreshToken, REFRESH_TOKEN_EXPIRATION_DAYS * 24 * 60, secure).toString());
        log.debug("Tokens added to response cookies");
    }

    private ResponseCookie buildCookie(String name, String value, long maxAgeMinutes, boolean secure) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(secure)
                .path("/")
                .sameSite(secure ? "None" : "Lax")
                .maxAge(TimeUnit.MINUTES.toSeconds(maxAgeMinutes))
                .build();
    }

    public void removeAuthCookies(HttpServletResponse response) {
        response.addHeader(HttpHeaders.SET_COOKIE, buildCookie(ACCESS_TOKEN_COOKIE_NAME, "", 0, true).toString());
        response.addHeader(HttpHeaders.SET_COOKIE, buildCookie(REFRESH_TOKEN_COOKIE_NAME, "", 0, true).toString());
        log.debug("Auth cookies removed");
    }

    // ==================== CLAIMS EXTRACTION ====================

    public List<String> getRolesFromToken(HttpServletRequest request, boolean fromCookie) {
        Claims claims = validateAndParseToken(request, fromCookie);
        @SuppressWarnings("unchecked")
        List<String> roles = claims.get("roles", List.class);
        return roles != null ? roles : Collections.emptyList();
    }

    public boolean hasRole(HttpServletRequest request, String roleName, boolean fromCookie) {
        return getRolesFromToken(request, fromCookie).contains(roleName);
    }

    public Long getUserIdFromToken(String token) {
        return parseToken(token).get("userId", Long.class);
    }

    public String getUsernameFromToken(String token) {
        return parseToken(token).getSubject();
    }
}
