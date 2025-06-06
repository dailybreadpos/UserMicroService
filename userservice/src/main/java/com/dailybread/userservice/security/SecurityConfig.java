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
                .requestMatchers(HttpMethod.POST, "/api/user/auth/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/user/signup").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/user/activate").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/user/register").hasAuthority("admin")
                .requestMatchers(HttpMethod.GET, "/api/user/cashiers").hasAuthority("admin")
                .requestMatchers(HttpMethod.DELETE, "/api/user/cashiers/{email}").hasAuthority("admin")
                .requestMatchers(HttpMethod.POST, "/api/user/forgot-password").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/user/verify-otp").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/user/reset-password").permitAll()

        )
                .addFilterBefore(jwtRequestFilter,
                        UsernamePasswordAuthenticationFilter.class);

        // disable CSRF
        http.csrf().disable();
        return http.build();
    }

}