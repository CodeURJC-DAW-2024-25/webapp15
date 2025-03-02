package com.stepx.stepx.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.stepx.stepx.model.User;
import com.stepx.stepx.repository.UserRepository;




@Service
public class RepositoryUserDetailsService implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;

	public RepositoryUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository; //consultas
    }

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByUsername(username)
		.orElseThrow(() -> {
			return new UsernameNotFoundException("User not found");
		});

		
		List<GrantedAuthority> roles = new ArrayList<>();//spring grantedAuthority
		for (String role : user.getRoles()) {
			roles.add(new SimpleGrantedAuthority("ROLE_" + role));//spring espera que los roles empiecen con ROLE_
		}

		return new org.springframework.security.core.userdetails.User(user.getUsername(), 
				user.getEncodedPassword(), roles);

	}
}