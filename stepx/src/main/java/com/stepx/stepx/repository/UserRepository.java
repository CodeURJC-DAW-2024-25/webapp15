package com.stepx.stepx.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stepx.stepx.model.ShoeSizeStock;
import com.stepx.stepx.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findById(Long id);
    Optional<User> findByUsername(String username);
    
}
