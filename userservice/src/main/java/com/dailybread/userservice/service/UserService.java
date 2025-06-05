package com.dailybread.userservice.service;

import java.util.List;

import com.dailybread.userservice.dto.CashierActivationRequest;
import com.dailybread.userservice.model.User;

public interface UserService {
    User save(User user);

    String login(String email, String password);

    String addCashier(String email, String userName);

    String activateCashier(CashierActivationRequest cashierActivationRequest);

    List<User> getAllCashiers();

    String deleteCashier(String email);

    public String forgotPassword(String email);

    public boolean verifyOtp(String email, String otp);

    public User resetPassword(String email, String newPassword);
}
