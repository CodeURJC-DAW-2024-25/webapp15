package com.stepx.stepx.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

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
		this.userRepository = userRepository; // requests in databse
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    List<GrantedAuthority> roles = new ArrayList<>();
    for (String role : user.getRoles()) {
        roles.add(new SimpleGrantedAuthority("ROLE_" + role));
    }

	Collection<? extends GrantedAuthority> grantedAuthorities = user.getAuthorities();

   List<String> rolesAsStrings = grantedAuthorities.stream()
    .map(GrantedAuthority::getAuthority) 
    .collect(Collectors.toList());

	user.setRoles(rolesAsStrings);
    return user; 
}
}