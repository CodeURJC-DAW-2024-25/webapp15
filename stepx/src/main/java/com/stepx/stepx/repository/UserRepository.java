package com.stepx.stepx.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stepx.stepx.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    
}
