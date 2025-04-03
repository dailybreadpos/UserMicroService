package com.dailybread.userservice.service;

import com.dailybread.userservice.dto.CashierActivationRequest;
import com.dailybread.userservice.model.User;

public interface UserService {
    User save(User user);

    String login(String email,String password);
    String addCashier(String email);
    String activateCashier(CashierActivationRequest cashierActivationRequest);
}
