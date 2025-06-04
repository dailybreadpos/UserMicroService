package com.dailybread.userservice.rest;

import com.dailybread.userservice.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.bind.annotation.RequestMapping;
import com.dailybread.userservice.service.AuthService;
import com.dailybread.userservice.service.JWTUtil;
import org.springframework.http.ResponseEntity;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user/auth")
public class AuthController {
    @Autowired
    private AuthService authService;
    @Autowired
    private JWTUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody User user) {
        System.out.println("Login request received for user: " + user.getEmail());
        UserDetails userDetails = authService.login(user.getEmail(), user.getPassword());
        if (userDetails == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid credentials"));
        }

        String jwt = jwtUtil.generateToken(userDetails);
        Map<String, Object> response = new HashMap<>();
        // userDetails.getAuthorities().forEach(authority -> {
        // System.out.println("Authority: " + authority.getAuthority());
        // role = authority.getAuthority();
        // });
        String role = userDetails.getAuthorities().stream().findFirst().map(auth -> auth.getAuthority()).orElse("user");
        response.put("jwt", jwt);
        response.put("user", userDetails);
        System.out.println("User role: " + role);
        // if (role.equals("admin")) {
        // // return new RedirectView("http://localhost:5173/dashboard");
        // response.put("redirect", "http://localhost:5173/dashboard");
        // }
        response.put("redirect",
                role.equals("admin") ? "http://localhost:5173/dashboard" : "http://localhost:5173/home");
        return ResponseEntity.ok(response);
    }
}
