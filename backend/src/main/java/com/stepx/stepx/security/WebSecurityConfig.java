package com.stepx.stepx.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.stepx.stepx.controller.web.CustomAuthenticationSuccessHandler;
import com.stepx.stepx.security.jwt.JwtRequestFilter;
import com.stepx.stepx.security.jwt.UnauthorizedHandlerJwt;

import aj.org.objectweb.asm.commons.Method;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

	// @Autowired
	private RepositoryUserDetailsService userDetailsService;

	@Autowired
	private JwtRequestFilter jwtRequestFilter;

	@Autowired
	private UnauthorizedHandlerJwt unauthorizedHandlerJwt;


	public WebSecurityConfig(RepositoryUserDetailsService userDetailsService) {
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
	@Order(1)
	public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
		
		http.authenticationProvider(authenticationProvider());
		
		http
			.securityMatcher("/api/**")
			.exceptionHandling(handling -> handling.authenticationEntryPoint(unauthorizedHandlerJwt));
		
		http
			.authorizeHttpRequests(authorize -> authorize
                    // PRIVATE ENDPOINTS
						//users
                    .requestMatchers(HttpMethod.POST,"/api/OrderItem/**", "/api/OrderShoes/**","/api/reviews/**","/api/ShoeSizeStock/**", "/api/user/**").hasRole("USER")
                    .requestMatchers(HttpMethod.PUT,"/api/OrderItem/**", "/api/reviews/**", "/api/ShoeSizeStock/**").hasRole("USER")
					.requestMatchers(HttpMethod.GET,"/api/OrderItem/**","/api/OrderShoes/**").hasRole("USER")
					.requestMatchers(HttpMethod.DELETE,"/api/admin/**", "/api/OrderItem/**", "/api/reviews/**", "/api/user/**").hasRole("USER")
						//Admins
                    .requestMatchers(HttpMethod.DELETE,"/api/admin/**","/api/reviews/**", "/api/Shoe/**", "/api/user/**").hasRole("ADMIN")
					.requestMatchers(HttpMethod.GET,"/api/admin/**", "/api/reviews/**","/api/Shoe/**", "/api/user/**").hasRole("ADMIN")
					.requestMatchers(HttpMethod.PUT,"/api/admin/**","/api/ShoeSizeStock/**").hasRole("ADMIN")
					.requestMatchers(HttpMethod.POST,"/api/admin/**", "/api/Shoe/**","/api/ShoeSizeStock/**").hasRole("ADMIN")
					// PUBLIC ENDPOINTS
					.requestMatchers("/api/shoe", "/**/image/**", "/api/Shoe/**").permitAll()
					.anyRequest().permitAll()
			);
		
        http.formLogin(formLogin -> formLogin.disable());

        http.csrf(csrf -> csrf.disable());

        http.httpBasic(httpBasic -> httpBasic.disable());

        http.sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	@Order(2)
	public SecurityFilterChain filterChain(HttpSecurity http, CustomAuthenticationSuccessHandler successHandler)
			throws Exception {

		http.authenticationProvider(authenticationProvider());

		http .authorizeHttpRequests(authorize -> authorize
						// PUBLIC PAGES
						.requestMatchers("/index", "/register-user", "/shop/**", "/partials/**", "/createAccount",
								"/errorPage", "/api/**", "/api/create-product")
						.permitAll()
						.requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
						// PRIVATE PAGES
						.requestMatchers( "/profile/orders", "/shop/single-product/loadMoreReviews","/user/updateInformation","/profile")
						.hasAnyRole("USER", "ADMIN")
						.requestMatchers("/OrderItem/addItem", "/checkout/**", "/user/**").hasAnyRole("USER")
						.requestMatchers("/edit-product/**", "/admin", "/create-product","/shop/delete/**").hasAnyRole("ADMIN"))
				.formLogin(formLogin -> formLogin
						.loginPage("/login")
						.defaultSuccessUrl("/index", true)
						.failureUrl("/?error=true") // Redirect to main paige in case of error
						.permitAll())
				.logout(logout -> logout
						.logoutSuccessUrl("/index")
						.logoutUrl("/logout")
						.invalidateHttpSession(true)
						.deleteCookies("JSESSIONID")
						.permitAll());

		return http.build();
	}

}
