package com.dailybread.userservice.service;

import java.util.Optional;
import java.util.UUID;

import javax.management.RuntimeErrorException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import com.dailybread.userservice.dto.CashierActivationRequest;
import com.dailybread.userservice.model.User;
import com.dailybread.userservice.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {
    
    private UserRepository userRepository;

    private JavaMailSender mailSender;
    @Autowired
    public UserServiceImpl(UserRepository userRepository,JavaMailSender mailSender){
        this.userRepository = userRepository;
        this.mailSender = mailSender;
    }
    @Override
    public User save(User user){
        return userRepository.save(user);
    }

    @Override
    public String login(String email,String password){
        Optional<User> user2 = userRepository.findByEmail(email);
        if(user2.isPresent()){
            User user = user2.get();
            if(password.equals(user.getPassword())){
                return "login successful";
            }else{
                // throw new RuntimeException("Invalid credentials");
                return "Invalid Credentials";
            } 
        }else{
            // throw new RuntimeException("Invalid credentials");
            return "Invalid Credentials";
        }
    }

    private String generateActivationToken(){
        return UUID.randomUUID().toString();
    }

    @Override
    public String addCashier(String email){
        Optional<User> user = userRepository.findByEmail(email);
        if(user.isPresent()){
            return "Cashier with this email is already added";
        }
        User user2 = new User();
        user2.setEmail(email);
        user2.setRole("cashier");

        String token = generateActivationToken();
        user2.setActivationToken(token);
        User newUser = userRepository.save(user2);

        sendInvitation(user2.getEmail(),user2.getActivationToken());
        return "added successfully";
    }

    private void sendInvitation(String email,String token){
        
        try{
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setFrom("11603002276pema@gmail.com");
            message.setSubject("Invitation");
            message.setText("http://localhost:5173/"+token);
            mailSender.send(message);
        }catch(Exception e){
            e.printStackTrace();
            
        }
    }

    @Override
    public String activateCashier(CashierActivationRequest cashierActivationRequest){
        User user = userRepository.findByActivationToken(cashierActivationRequest.getActivationToken()).orElseThrow(() -> new RuntimeException("Invalid activation token"));
     
        if(user.isActive()){
            return "Account already activated";
        }
        user.setPassword(cashierActivationRequest.getPassword());
        user.setActive(true);
        user.setActivationToken(null);
        userRepository.save(user);
        return "Activated Account.You can login";
    }
}
