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

        
        ResponseEntity<AuthResponse> result = userService.login(response, loginRequest);
        
        System.out.println("[TRACE BACK] Respuesta de /login: " + 
            (result.getBody() != null ? result.getBody().getStatus() : "null"));

        return result;
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser(@AuthenticationPrincipal User user) {
 
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            UserDTO userDto = userMapper.toDTO(user);

            return ResponseEntity.ok(userDto);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(
            @CookieValue(name = "RefreshToken", required = false) String refreshToken, 
            HttpServletResponse response) {
        
        ResponseEntity<AuthResponse> result = userService.refresh(response, refreshToken);
        
        return result;
    }

    @PostMapping("/logout")
	public ResponseEntity<AuthResponse> logOut(HttpServletResponse response) {
		return ResponseEntity.ok(new AuthResponse(Status.SUCCESS, userService.logout(response)));
	}

}