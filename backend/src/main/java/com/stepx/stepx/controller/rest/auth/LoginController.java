package com.stepx.stepx.controller.rest.auth;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.stepx.stepx.security.jwt.AuthResponse.Status;
import com.stepx.stepx.security.jwt.LoginRequest;
import com.stepx.stepx.security.jwt.UserLoginService;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/v1/auth")
public class LoginController {

    @Autowired
    private UserLoginService userService;

    @Autowired
    private UserMapper userMapper;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody LoginRequest loginRequest,
            HttpServletResponse response) {

        System.out.println("[TRACE BACK] Llamada a /login recibida");
        System.out.println("Usuario recibido: " + loginRequest.getUsername());

        ResponseEntity<AuthResponse> result = userService.login(response, loginRequest);

        System.out.println("[TRACE BACK] Respuesta de /login: " +
                (result.getBody() != null ? result.getBody().getStatus() : "null"));

        System.out.println("[TRACE BACK] Estado de resultado retornado por controller /login: " + result.toString());

        return result;
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser(@AuthenticationPrincipal User user) {
        System.out.println("[TRACE BACK] Llamada a /me recibida");

        if (user == null) {
            System.out.println("[ERROR BACK] Usuario no autenticado en /me");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        System.out.println("[TRACE BACK] Usuario autenticado: " + user.getUsername() +
                " ID: " + user.getId());

        try {
            UserDTO userDto = userMapper.toDTO(user);
            System.out.println("[TRACE BACK] Datos usuario a devolver: " +
                    userDto.toString());

            return ResponseEntity.ok(userDto);
        } catch (Exception e) {
            System.out.println("[ERROR BACK] Error en /me: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(
            @CookieValue(name = "RefreshToken", required = false) String refreshToken,
            HttpServletResponse response) {

        System.out.println("[TRACE BACK] Llamada a /refresh recibida");
        System.out.println("Refresh token recibido: " + (refreshToken != null ? "presente" : "ausente"));

        ResponseEntity<AuthResponse> result = userService.refresh(response, refreshToken);

        System.out.println("[TRACE BACK] Resultado refresh: " +
                (result.getBody() != null ? result.getBody().getStatus() : "null"));

        return result;
    }

    @PostMapping("/logout")
    public ResponseEntity<AuthResponse> logOut(HttpServletResponse response) {
        return ResponseEntity.ok(new AuthResponse(Status.SUCCESS, userService.logout(response)));
    }

    @GetMapping("/check")
    public ResponseEntity<Boolean> checkAuthenticated(@AuthenticationPrincipal User user) {
        if (user != null) {
            System.out.println("[TRACE BACK] Usuario autenticado en /check: " + user.getUsername());
            return ResponseEntity.ok(true);
        } else {
            System.out.println("[TRACE BACK] Usuario no autenticado en /check");
            return ResponseEntity.ok(false);
        }
    }

}