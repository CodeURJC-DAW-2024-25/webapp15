package com.stepx.stepx.security;

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
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.stepx.stepx.controller.web.CustomAuthenticationSuccessHandler;
import com.stepx.stepx.security.jwt.JwtRequestFilter;
import com.stepx.stepx.security.jwt.UnauthorizedHandlerJwt;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final RepositoryUserDetailsService userDetailsService;
    private final JwtRequestFilter jwtRequestFilter;
    private final UnauthorizedHandlerJwt unauthorizedHandlerJwt;
    private final AccessDeniedHandler accessDeniedHandler;

    public SecurityConfig(RepositoryUserDetailsService userDetailsService,
                        JwtRequestFilter jwtRequestFilter,
                        UnauthorizedHandlerJwt unauthorizedHandlerJwt,
                        AccessDeniedHandler accessDeniedHandler) {
        this.userDetailsService = userDetailsService;
        this.jwtRequestFilter = jwtRequestFilter;
        this.unauthorizedHandlerJwt = unauthorizedHandlerJwt;
        this.accessDeniedHandler = accessDeniedHandler;
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

    // Configuración para endpoints API (JWT)
    @Bean
    @Order(1)
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/api/v1/**")
            .authenticationProvider(authenticationProvider())
            .exceptionHandling(handling -> handling
                .authenticationEntryPoint(unauthorizedHandlerJwt)
                .accessDeniedHandler(accessDeniedHandler)
            )
            .authorizeHttpRequests(authorize -> authorize
                // Endpoints públicos
                .requestMatchers(
                    "/api/v1/auth/**",
                    "/api/v1/Shoes",
                    "/api/v1/image/**",
                    "/api/v1/Shoe/**",
                    "/api/v1/reviews/**",
                    "/api/v1/user/*/image"
                ).permitAll()
                
                // Endpoints de usuario
                .requestMatchers(HttpMethod.POST, 
                    "/api/v1/OrderItem/**", 
                    "/api/v1/OrderShoes/**", 
                    "/api/v1/reviews", 
                    "/api/v1/ShoeSizeStock/**", 
                    "/api/v1/user/**"
                ).hasRole("USER")
                
                .requestMatchers(HttpMethod.PUT, 
                    "/api/v1/OrderItem/**", 
                    "/api/v1/reviews/**", 
                    "/api/v1/ShoeSizeStock/**"
                ).hasRole("USER")
                
                .requestMatchers(HttpMethod.GET, 
                    "/api/v1/OrderItem/**", 
                    "/api/v1/coupon/**",
                    "/api/v1/user/chartuser/**", 
                    "/api/v1/OrderShoes/**",
                    "/api/v1/user/{id}/image"
                ).hasRole("USER")
                
                .requestMatchers(HttpMethod.DELETE, 
                    "/api/v1/OrderItem/**", 
                    "/api/v1/reviews/**", 
                    "/api/v1/user/**"
                ).hasRole("USER")
                
                // Endpoints de admin
                .requestMatchers(
                    "/api/v1/admin/**",
                    "/api/v1/coupon/**",
                    "/api/v1/reviews/**",
                    "/api/v1/Shoe/**",
                    "/api/v1/user/**",
                    "/api/v1/ShoeSizeStock/**"
                ).hasRole("ADMIN")
                
                .anyRequest().authenticated()
            )
            .formLogin(form -> form.disable())
            .httpBasic(httpBasic -> httpBasic.disable())
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Configuración para páginas web (form login)
    @Bean
    @Order(2)
    public SecurityFilterChain webFilterChain(HttpSecurity http, CustomAuthenticationSuccessHandler successHandler) throws Exception {
        http
            .authenticationProvider(authenticationProvider())
            .authorizeHttpRequests(authorize -> authorize
                // Páginas públicas
                .requestMatchers(
                    "/",
                    "/index",
                    "/register-user",
                    "/shop/**",
                    "/partials/**",
                    "/createAccount",
                    "/errorPage",
                    "/login",
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/css/**",
                    "/js/**",
                    "/images/**"
                ).permitAll()
                
                // Páginas de usuario
                .requestMatchers(
                    "/profile/**",
                    "/shop/single-product/loadMoreReviews",
                    "/user/updateInformation",
                    "/OrderItem/addItem",
                    "/checkout/**",
                    "/user/**"
                ).hasAnyRole("USER", "ADMIN")
                
                // Páginas de admin
                .requestMatchers(
                    "/edit-product/**",
                    "/admin/**",
                    "/create-product",
                    "/shop/delete/**"
                ).hasRole("ADMIN")
                
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .successHandler(successHandler)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/index")
                .logoutUrl("/logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID", "AccessToken", "RefreshToken")
                .permitAll()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
            );

        return http.build();
    }

    // Configuración CORS (aunque uses proxy, es buena práctica tenerlo)
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("http://localhost:4200");
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}