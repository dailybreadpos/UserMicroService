package com.dailybread.userservice.security;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.dailybread.userservice.model.User;
import com.dailybread.userservice.repository.UserRepository;
import com.dailybread.userservice.security.DailyUserDetails;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository; // Your MongoRepository

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        System.out.println("User found: " + user.getEmail());
        return new DailyUserDetails(user);
        // return new
        // org.springframework.security.core.userdetails.User(user.getEmail(),
        // user.getPassword());
    }
}
