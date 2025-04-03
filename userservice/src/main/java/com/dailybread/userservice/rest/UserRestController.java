package com.dailybread.userservice.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dailybread.userservice.dto.CashierActivationRequest;
import com.dailybread.userservice.dto.LoginRequest;
import com.dailybread.userservice.model.User;
import com.dailybread.userservice.service.UserService;

import jakarta.websocket.server.PathParam;

@RestController
@RequestMapping("/api")
public class UserRestController {
    
    private UserService userService;

    @Autowired
    public UserRestController(UserService userService){
        this.userService = userService;
    }

    @PostMapping("/signup")
    public User save(@RequestBody User user){
        return userService.save(user);
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest loginRequest){
        return userService.login(loginRequest.getEmail(),loginRequest.getPassword());
    }
    @PostMapping("/register")
    public void addCashier(@RequestParam String email ){
        System.out.println(email+" email received");
        userService.addCashier(email);
    }
    @PostMapping("/activate")
    public void activateCashier(@RequestBody CashierActivationRequest request){
        userService.activateCashier(request);
    }
}
