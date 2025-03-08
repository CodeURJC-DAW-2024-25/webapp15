 package com.stepx.stepx.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.stepx.stepx.controller.CustomAuthenticationSuccessHandler;



@Configuration
@EnableWebSecurity
public class WebSecurityConfig{
    
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
			.requestMatchers( "/index", "/register-user", "/shop/**", "/shop/single-product/**", "/partials/**", "/createAccount").permitAll()
            .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
            // PRIVATE PAGES
			.requestMatchers("/profile","/profile/orders").hasAnyRole("USER", "ADMIN")
            .requestMatchers("/OrderItem/addItem","/checkout/**","/user/**").hasAnyRole("USER")
            .requestMatchers("/edit-product/**", "/admin", "/create-product").hasAnyRole("ADMIN")
        )
        .formLogin(formLogin -> formLogin
            .loginPage("/login")
            .defaultSuccessUrl("/index", true)
            .failureUrl("/login?error=true")  // Redirect to main paige in case of error
            .permitAll()
        ) 
        .logout(logout -> logout
			.logoutSuccessUrl("/index")
			.logoutUrl("/logout")
			.invalidateHttpSession(true) 
            .deleteCookies("JSESSIONID")
            .permitAll()
        );

		return http.build();
	}

}
