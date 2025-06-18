package com.stepx.stepx.security.jwt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.stepx.stepx.dto.UserDTO;
import com.stepx.stepx.mapper.UserMapper;
import com.stepx.stepx.model.User;
import com.stepx.stepx.repository.UserRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class UserLoginService {

    private static final Logger log = LoggerFactory.getLogger(UserLoginService.class);

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserLoginService(
            AuthenticationManager authenticationManager,
            UserDetailsService userDetailsService,
            JwtTokenProvider jwtTokenProvider,
            UserRepository userRepository,
            UserMapper userMapper) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public ResponseEntity<AuthResponse> login(HttpServletResponse response, LoginRequest loginRequest) {
        try {
            // 1. Autenticar credenciales
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(), loginRequest.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 2. Cargar detalles del usuario
            UserDetails user = userDetailsService.loadUserByUsername(loginRequest.getUsername());

            // 3. Generar tokens
            String accessToken = jwtTokenProvider.generateAccessToken(user);
            String refreshToken = jwtTokenProvider.generateRefreshToken(user);

            // 4. Agregar tokens a las cookies
            jwtTokenProvider.addTokensToResponse(response, accessToken, refreshToken, true);

            return ResponseEntity.ok(new AuthResponse(
                    AuthResponse.Status.SUCCESS,
                    "Authentication successful. Tokens set in cookies.",
                    accessToken
            ));
        } catch (Exception e) {
            log.error("Login error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse(AuthResponse.Status.FAILURE, "Invalid credentials"));
        }
    }

    public ResponseEntity<UserDTO> getCurrentUser(HttpServletRequest request) {
        try {
            // 1. Validar token y extraer claims
            Claims claims = jwtTokenProvider.validateAndParseToken(request, true);
            String username = claims.getSubject();

            // 2. Buscar usuario
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            // 3. Retornar DTO
            return ResponseEntity.ok(userMapper.toDTO(user));
        } catch (JwtException | UsernameNotFoundException e) {
            log.error("Error retrieving current user: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    public ResponseEntity<AuthResponse> refresh(HttpServletRequest request, HttpServletResponse response) {
        try {
            // 1. Validar y parsear token desde cookie
            Claims claims = jwtTokenProvider.validateAndParseToken(request, true);

            // 2. Verificar tipo de token
            String tokenType = claims.get("type", String.class);
            if (!"REFRESH".equals(tokenType)) {
                throw new JwtException("Token is not a refresh token");
            }

            // 3. Cargar usuario y generar nuevo token de acceso
            UserDetails user = userDetailsService.loadUserByUsername(claims.getSubject());
            String newAccessToken = jwtTokenProvider.generateAccessToken(user);

            // 4. Actualizar cookie del access token
            jwtTokenProvider.addTokensToResponse(response, newAccessToken, null, true);

            return ResponseEntity.ok(new AuthResponse(
                    AuthResponse.Status.SUCCESS,
                    "Access token refreshed successfully",
                    newAccessToken
            ));

        } catch (JwtException e) {
            log.error("Token refresh error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse(AuthResponse.Status.FAILURE, "Invalid refresh token"));
        }
    }

    public String logout(HttpServletResponse response) {
        SecurityContextHolder.clearContext();
        jwtTokenProvider.removeAuthCookies(response);
        return "Logout successful";
    }
}