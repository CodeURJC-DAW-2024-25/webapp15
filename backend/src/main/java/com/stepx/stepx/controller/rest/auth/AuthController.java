package com.stepx.stepx.controller.rest.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stepx.stepx.dto.UserDTO;
import com.stepx.stepx.mapper.UserMapper;
import com.stepx.stepx.model.User;
import com.stepx.stepx.security.jwt.AuthResponse;
import com.stepx.stepx.security.jwt.LoginRequest;
import com.stepx.stepx.security.jwt.UserLoginService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final UserLoginService userService;
    private final UserMapper userMapper;

    public AuthController(UserLoginService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    // ========== ENDPOINTS PÚBLICOS ==========

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody LoginRequest loginRequest,
            HttpServletResponse response) {
        logger.info("Login request for user: {}", loginRequest.getUsername());

        try {
            ResponseEntity<AuthResponse> result = userService.login(response, loginRequest);
            logger.debug("Login result: {}", result.getBody() != null ? result.getBody().getStatus() : "null");
            return result;
        } catch (Exception e) {
            logger.error("Error during login", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new AuthResponse(AuthResponse.Status.FAILURE, "Error during login"));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(
            HttpServletRequest request,
            HttpServletResponse response) {
        logger.info("Refresh token request received");

        try {
            ResponseEntity<AuthResponse> result = userService.refresh(request, response);
            logger.debug("Refresh result: {}", result.getBody() != null ? result.getBody().getStatus() : "null");
            return result;
        } catch (Exception e) {
            logger.error("Error refreshing token", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new AuthResponse(AuthResponse.Status.FAILURE, "Error refreshing token"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<AuthResponse> logout(HttpServletResponse response) {
        logger.info("Logout request received");

        try {
            String result = userService.logout(response);
            return ResponseEntity.ok(new AuthResponse(AuthResponse.Status.SUCCESS, result));
        } catch (Exception e) {
            logger.error("Error during logout", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new AuthResponse(AuthResponse.Status.FAILURE, "Error during logout"));
        }
    }

    // ========== ENDPOINTS PROTEGIDOS ==========

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser(@AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        logger.debug("Fetching data for authenticated user: {}", user.getUsername());

        try {
            UserDTO userDto = userMapper.toDTO(user);
            return ResponseEntity.ok(userDto);
        } catch (Exception e) {
            logger.error("Error mapping user to DTO", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/check")
    public ResponseEntity<Boolean> checkAuthStatus(
            @AuthenticationPrincipal User user,
            @CookieValue(name = "RefreshToken", required = false) String refreshToken) {

        boolean isAuthenticated = user != null;
        logger.debug("Auth check - Authenticated: {}", isAuthenticated);
        return ResponseEntity.ok(isAuthenticated);
    }
}

