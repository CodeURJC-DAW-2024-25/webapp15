 package com.stepx.stepx.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import com.stepx.stepx.controller.CustomAuthenticationSuccessHandler;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;



@Configuration
@EnableWebSecurity
public class WebSecurityConfig{

	private static final Logger logger = LoggerFactory.getLogger(RepositoryUserDetailsService.class);
    
	//@Autowired
    private RepositoryUserDetailsService userDetailsService;

	public WebSecurityConfig(RepositoryUserDetailsService userDetailsService){
		this.userDetailsService = userDetailsService;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

		authProvider.setUserDetailsService(userDetailsService);
		authProvider.setPasswordEncoder(passwordEncoder()); 

		return authProvider;
	}

	@Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http, CustomAuthenticationSuccessHandler successHandler) throws Exception {
		
		http.authenticationProvider(authenticationProvider());
		
		http
			.authorizeHttpRequests(authorize -> authorize
			// PUBLIC PAGES
			.requestMatchers( "/index", "/register-user", "/shop", "/shop/single-product/**").permitAll()
            .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
            // PRIVATE PAGES
            .requestMatchers("/profile").hasAnyRole("USER")
            .requestMatchers("/edit-product", "/admin-pannel").hasAnyRole("ADMIN")
        )
        .formLogin(formLogin -> formLogin
            .loginPage("/login") 
			.successHandler(successHandler)
            .defaultSuccessUrl("/index", true)
            .failureUrl("/login?error=true")  // Redirige a la página principal con un parámetro de error
            .permitAll()
        )
        .logout(logout -> logout
			.logoutSuccessUrl("/index")
            .permitAll()
        );

    // Disable CSRF at the moment
    http.csrf(csrf -> csrf.disable());

		return http.build();
	}

}
