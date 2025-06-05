package com.dailybread.userservice.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.management.RuntimeErrorException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.dailybread.userservice.dto.CashierActivationRequest;
import com.dailybread.userservice.model.User;
import com.dailybread.userservice.repository.UserRepository;
import com.dailybread.userservice.util.JWT;
import com.dailybread.userservice.util.OTP;
import com.dailybread.userservice.util.OTPUtil;
import com.dailybread.userservice.exception.UserNotFoundException;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private JavaMailSender mailSender;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    @Lazy
    public UserServiceImpl(UserRepository userRepository, JavaMailSender mailSender,
            BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.mailSender = mailSender;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User save(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public String login(String email, String password) {
        Optional<User> user2 = userRepository.findByEmail(email);
        if (user2.isPresent()) {
            User user = user2.get();
            if (password.equals(user.getPassword())) {
                return "login successful";
            } else {
                // throw new RuntimeException("Invalid credentials");
                return "Invalid Credentials";
            }
        } else {
            // throw new RuntimeException("Invalid credentials");
            return "Invalid Credentials";
        }
    }

    private String generateActivationToken() {
        return UUID.randomUUID().toString();
    }

    @Override
    public String addCashier(String email, String userName) {
        try {
            System.out.println("adding cashier now");
            Optional<User> user = userRepository.findByEmail(email);
            // if(user.isPresent()){
            // System.out.println("email is already added");
            // return "Cashier with this email is already added";
            // }
            System.out.println(userName + " user name");
            User user2 = new User();
            user2.setUserName(userName);
            user2.setEmail(email);
            user2.setRole("cashier");
            user2.setPassword("");

            // String token = generateActivationToken();
            String token = JWT.generateToken(email);
            // user2.setActivationToken(token);
            User newUser = userRepository.save(user2);

            sendInvitation(user2.getEmail(), token);
            return "success";
        } catch (Exception e) {
            System.out.println("error in adding cashier");
            return "error in adding cashier";
        }
    }

    private void sendInvitation(String email, String token) {

        try {
            System.out.println("sendding invitation link");
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setFrom("11603002276pema@gmail.com");
            message.setSubject("Invitation");
            message.setText("http://localhost:5173/activate?token=" + token);
            mailSender.send(message);
            System.out.println("email setn with " + token);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String activateCashier(CashierActivationRequest cashierActivationRequest) {
        String email = JWT.validateToken(cashierActivationRequest.getActivationToken());
        System.out.println(email);
        if (email == "invalid token") {
            return "invalid token!";
        }
        Optional<User> user = userRepository.findByEmail(email);
        // User user =
        // userRepository.findByActivationToken(cashierActivationRequest.getActivationToken()).orElseThrow(()
        // -> new RuntimeException("Invalid activation token"));

        if (user.isEmpty()) {
            return "You are not invited by admin";
        }

        User user2 = user.get();
        if (user2.isActive()) {
            return "Email is already activated. You can login";
        }
        user2.setPassword(passwordEncoder.encode(cashierActivationRequest.getPassword()));
        user2.setActive(true);
        userRepository.save(user2);
        return "success";
    }

    @Override
    public List<User> getAllCashiers() {
        return userRepository.findAllByRole("cashier");
    }

    @Override
    public String deleteCashier(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            userRepository.delete(user.get());
            return "success";
        } else {
            return "Cashier not found";
        }

    }

    @Override
    public String forgotPassword(String email) {

        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw new UserNotFoundException(email + " not registed with us");
        }
        OTP otp = OTPUtil.generateOtp(6, 5);
        User existingUser = user.get();
        existingUser.setOtp(otp);
        // existingUser.setOtpExpiry(otp.getExpiryTime());
        userRepository.save(existingUser);
        System.out.println("OTP generated for user: " + email + " is " + otp.getOtp());
        sendMail(email, otp);
        return otp.getOtp();
    }

    @Override
    public boolean verifyOtp(String email, String otp) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw new UserNotFoundException("Some error occurred while verifying. Retry again later.");
        }
        User existingUser = user.get();
        OTP existingOtp = existingUser.getOtp();

        if (existingOtp == null || !existingOtp.getOtp().equals(otp)) {
            System.out.println("Invalid OTP for user: " + email);
            return false;
        }

        // Check if the OTP is expired
        if (existingOtp.isExpired()) {
            System.out.println("OTP expired for user: " + email);
            return false;
        }

        // OTP is valid, clear it after verification
        existingUser.setAuthenticatedForPasswordReset(true);
        existingUser.setOtp(null);
        userRepository.save(existingUser);

        System.out.println("OTP verified successfully for user: " + email);
        return true;
    }

    @Override
    public User resetPassword(String email, String newPassword) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw new UserNotFoundException("Some error occurred while resetting password. Retry again later.");
        }

        User existingUser = user.get();
        if (!existingUser.isAuthenticatedForPasswordReset()) {
            throw new UserNotFoundException("User not authenticated for password reset: " + email);
        }

        existingUser.setPassword(passwordEncoder.encode(newPassword));
        existingUser.setAuthenticatedForPasswordReset(false); // Reset the authentication flag
        return userRepository.save(existingUser);
    }

    private void sendMail(String to, OTP otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setFrom("11603002276pema@gmail.com");
        message.setSubject("OTP for Password Reset");
        message.setText("Your OTP for password reset is: " + otp.getOtp() + ". It is valid for 5 minutes.");
        try {
            mailSender.send(message);
            System.out.println("OTP sent to " + to);
        } catch (Exception e) {
            System.out.println("Error sending OTP email: " + e.getMessage());
            throw new RuntimeErrorException(null, "Failed to send OTP email");
        }
    }
}
