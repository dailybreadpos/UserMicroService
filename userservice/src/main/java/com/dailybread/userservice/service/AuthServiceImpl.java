package com.dailybread.userservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import com.dailybread.userservice.service.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.BadCredentialsException;

@Service
public class AuthServiceImpl implements AuthService {
    // @Autowired
    // private AuthenticationManager authenticationManager;
    // @Autowired
    // private UserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    @Autowired
    public AuthServiceImpl(@Lazy AuthenticationManager authenticationManager,
            @Lazy UserDetailsService userDetailsService) {
        this.authenticationManager = authenticationManager;

        this.userDetailsService = userDetailsService;
    }

    @Override
    public UserDetails login(String email, String password) {
        // System.out.println("show: " +
        // authenticationManager.authenticate(newUsernamePasswordAuthenticationToken(email,
        // password)));
        try {
            System.out.println("Authenticating user: " + email);
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
            System.out.println("Authentication successful");
            return userDetailsService.loadUserByUsername(email);
        } catch (BadCredentialsException e) {
            System.out.println("Invalid Credentials");
            return null;
        } catch (Exception e) {
            System.out.println("Authentication failed: " + e.getMessage());
            return null;
        }
    }
}