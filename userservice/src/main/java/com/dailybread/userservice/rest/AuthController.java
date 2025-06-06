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
import com.dailybread.userservice.service.RecaptchaService;
import com.dailybread.userservice.dto.LoginRequest;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.http.HttpStatus;
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

    @Autowired
    private RecaptchaService recaptchaService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest user, HttpServletResponse response) {
        if (!recaptchaService.isCaptchaValid(user.getRecaptchaToken())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "CAPTCHA verification failed"));
        }
        System.out.println("Login request received for user: " + user.getEmail());
        UserDetails userDetails = authService.login(user.getEmail(), user.getPassword());
        if (userDetails == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid credentials"));
        }

        String jwt = jwtUtil.generateToken(userDetails);
        Cookie cookie = new Cookie("token", jwt);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(24 * 60 * 60);

        response.setHeader("Set-Cookie",
                String.format("token=%s; Max-Age=86400; Path=/;HttpOnly; SameSite=Lazy", jwt));

        Map<String, Object> body = new HashMap<>();
        // userDetails.getAuthorities().forEach(authority -> {
        // System.out.println("Authority: " + authority.getAuthority());
        // role = authority.getAuthority();
        // });
        String role = userDetails.getAuthorities().stream().findFirst().map(auth -> auth.getAuthority()).orElse("user");
        // response.put("jwt", jwt);

        body.put("user", userDetails);
        System.out.println("User role: " + role);
        // if (role.equals("admin")) {
        // // return new RedirectView("http://localhost:5173/dashboard");
        // response.put("redirect", "http://localhost:5173/dashboard");
        // }
        body.put("redirect",
                role.equals("admin") ? "http://localhost:5173/dashboard" : "http://localhost:5173/home");
        return ResponseEntity.ok(body);
    }
}
