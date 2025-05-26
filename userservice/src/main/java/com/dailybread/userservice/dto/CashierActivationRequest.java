package com.dailybread.userservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CashierActivationRequest {
    
    private String email;
    private String activationToken;
    private String password;
    private String password2;
}
