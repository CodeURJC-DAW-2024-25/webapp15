package com.stepx.stepx.security.jwt;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.stepx.stepx.model.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtTokenProvider {

	private final SecretKey jwtSecret = Jwts.SIG.HS256.key().build();
	private final JwtParser jwtParser = Jwts.parser().verifyWith(jwtSecret).build();

	public String tokenStringFromHeaders(HttpServletRequest req){
		String bearerToken = req.getHeader(HttpHeaders.AUTHORIZATION);
		if (bearerToken == null) {
			throw new IllegalArgumentException("Missing Authorization header");
		}
		if(!bearerToken.startsWith("Bearer ")){
			throw new IllegalArgumentException("Authorization header does not start with Bearer: " + bearerToken);
		}
		return bearerToken.substring(7);
	}

	private String tokenStringFromCookies(HttpServletRequest request) {
		var cookies = request.getCookies();
		if (cookies == null) {
			throw new IllegalArgumentException("No cookies found in request");
		}

		for (Cookie cookie : cookies) {
			if (TokenType.ACCESS.cookieName.equals(cookie.getName())) {
				String accessToken = cookie.getValue();
				if (accessToken == null) {
					throw new IllegalArgumentException("Cookie %s has null value".formatted(TokenType.ACCESS.cookieName));
				}

				return accessToken;
			}
		}
		throw new IllegalArgumentException("No access token cookie found in request");
	}

	public Claims validateToken(HttpServletRequest req, boolean fromCookie){
		var token = fromCookie?
				tokenStringFromCookies(req):
				tokenStringFromHeaders(req);
		return validateToken(token);
	}

	public Claims validateToken(String token) {
		return jwtParser.parseSignedClaims(token).getPayload();
	}

	public String generateAccessToken(UserDetails userDetails) {
        System.out.println("[TRACE BACK] Generando access token para: " + userDetails.getUsername());
        String token = buildToken(TokenType.ACCESS, userDetails).compact();
        System.out.println("Token generado: " + token.substring(0, 10) + "...");
        return token;
    }

	public String generateRefreshToken(UserDetails userDetails) {
		var token = buildToken(TokenType.REFRESH, userDetails);
        return token.compact();
	}

	// public void addTokenToResponse(HttpServletResponse response, String accessToken, String refreshToken) {
    //     // Crear las cookies para los tokens
    //     var accessTokenCookie = ResponseCookie.from(TokenType.ACCESS.cookieName, accessToken)
    //         .httpOnly(true)
    //         .secure(true) // Si estás utilizando HTTPS, si no, remueve esto en desarrollo
    //         .path("/")
    //         .sameSite("None") // Importante para que funcione entre dominios
    //         .maxAge(TokenType.ACCESS.duration)
    //         .build();

    //     var refreshTokenCookie = ResponseCookie.from(TokenType.REFRESH.cookieName, refreshToken)
    //         .httpOnly(true)
    //         .secure(true) // Si estás utilizando HTTPS, si no, remueve esto en desarrollo
    //         .path("/")
    //         .sameSite("None") // Importante para que funcione entre dominios
    //         .maxAge(TokenType.REFRESH.duration)
    //         .build();

    //     // Agregar las cookies a la respuesta
    //     response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
    //     response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
    // }

	public void addTokenToResponse(HttpServletResponse response, String accessToken, String refreshToken) {
        System.out.println("[TRACE BACK] Generando cookies para tokens");
        System.out.println("Access Token: " + accessToken.substring(0, 10) + "...");
        System.out.println("Refresh Token: " + refreshToken.substring(0, 10) + "...");

        ResponseCookie accessCookie = ResponseCookie.from("AccessToken", accessToken)
            .httpOnly(true)
            .path("/")
            .maxAge(7 * 24 * 60 * 60)
            .build();

        ResponseCookie refreshCookie = ResponseCookie.from("RefreshToken", refreshToken)
            .httpOnly(true)
            .path("/")
            .maxAge(7 * 24 * 60 * 60)
            .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
        
        System.out.println("[TRACE BACK] Cookies establecidas en respuesta");
    }


	private JwtBuilder buildToken(TokenType tokenType, UserDetails userDetails) {
		var currentDate = new Date();
		var expiryDate = Date.from(new Date().toInstant().plus(tokenType.duration));
		Long userId = null;

        userId = ((User) userDetails).getId();
		System.out.println("Adjunto el id del usuario autenticado: " + userId);
    	
		
		return Jwts.builder()
			.claim("userId", userId)
			.claim("roles", userDetails.getAuthorities())
			.claim("type", tokenType.name())
			.subject(userDetails.getUsername())
			.issuedAt(currentDate)
			.expiration(expiryDate)
			.signWith(jwtSecret);
	}

	public List<String> getRolesFromToken(HttpServletRequest request, boolean fromCookie) {
		Claims claims = validateToken(request, fromCookie);
		@SuppressWarnings("unchecked")
		List<String> roles = claims.get("roles", List.class);
		return roles != null ? roles : Collections.emptyList();
	}

	public boolean hasRole(HttpServletRequest request, String roleName, boolean fromCookie) {
		return getRolesFromToken(request, fromCookie).contains(roleName);
	}
}