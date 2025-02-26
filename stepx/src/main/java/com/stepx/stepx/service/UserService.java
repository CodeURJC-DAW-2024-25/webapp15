package com.stepx.stepx.service;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.stepx.stepx.model.User;
import com.stepx.stepx.model.Review;

import com.stepx.stepx.repository.UserRepository;


@Service
public class UserService {
    
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    //public List<Review> getReviews(long id) {
      //  return reviewRepository.findById(id);
    //}

    public Optional<User> findUserById(Long userId) {
        return userRepository.findById(userId);
    }

}