package com.stepx.stepx.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.stepx.stepx.model.User;
import com.mysql.cj.jdbc.Blob;
import com.stepx.stepx.model.Review;

import com.stepx.stepx.repository.UserRepository;


@Service
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    //public List<Review> getReviews(long id) {
      //  return reviewRepository.findById(id);
    //}

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
            return passwordEncoder.matches(password, user.getEncodedPassword()); // ✅ Ahora funciona
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


}