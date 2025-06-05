package com.dailybread.userservice.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.BadCredentialsException;
import com.dailybread.userservice.security.JwtRequestFilter;
import com.dailybread.userservice.security.CustomUserDetailsService;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    public SecurityConfig() {
        System.out.println("SecurityConfig constructor called");
    }

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    // @Bean
    // public AuthenticationManager customAuthenticationManager() throws Exception {
    // System.out.println("H2i ");
    // return new ProviderManager(Arrays.asList(authProvider()));
    // }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        System.out.println("H2i ");
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authProvider() {
        System.out.println("Hi ");
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        System.out.println("UserDetailsService: " +
                userDetailsService.getClass().getName());
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(configurer -> configurer
                .requestMatchers(HttpMethod.GET, "/").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/signup").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/forgot-password").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/reset-password").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/verify-otp").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/activate").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/register").hasAuthority("admin")
                .requestMatchers(HttpMethod.GET, "/api/cashiers").hasAnyAuthority("admin")
                .requestMatchers(HttpMethod.DELETE, "/api/cashiers/{email}").hasAnyAuthority("admin")

        )
                .addFilterBefore(jwtRequestFilter,
                        UsernamePasswordAuthenticationFilter.class);

        // disable CSRF
        http.csrf().disable();
        return http.build();
    }

}