package com.dailybread.userservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OTPRequest {
    private String email;
    private String otp;
}
