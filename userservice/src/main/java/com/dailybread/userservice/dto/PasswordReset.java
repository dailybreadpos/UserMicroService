package com.dailybread.userservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordReset {
    private String email;
    private String password;
    private String confirmPassword;
}
