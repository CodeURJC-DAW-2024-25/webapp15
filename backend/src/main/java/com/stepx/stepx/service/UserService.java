package com.stepx.stepx.service;



import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

import com.stepx.stepx.dto.UserDTO;
import com.stepx.stepx.mapper.UserMapper;
import com.stepx.stepx.model.User;
import com.mysql.cj.jdbc.Blob;

import com.stepx.stepx.repository.UserRepository;


@Service
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder ,UserMapper userMapper ){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;

    }

    public Optional<User> findUserById(Long userId) {
        return userRepository.findById(userId);
    }

    
    public Optional<User> findUserByUserName(String username) {
        return userRepository.findByUsername(username);
    }


    public boolean authenticate(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            return passwordEncoder.matches(password, user.getEncodedPassword());
        }
        return false;
    }

    public void registerUser(String username,String email, String password, Blob image, String... roles) {
        String encodedPassword = passwordEncoder.encode(password);
        User newUser = new User(username, email,encodedPassword, image, roles);
        userRepository.save(newUser);
    }

    public void saveUser(User user){
        userRepository.save(user);
    }

    public UserDTO getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
            throw new RuntimeException("No authenticated user found");
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // Buscar usuario en la base de datos
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Convertir User a UserDTO
        return userMapper.toDTO(user);
    }

}