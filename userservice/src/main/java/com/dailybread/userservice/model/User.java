package com.dailybread.userservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import com.dailybread.userservice.util.OTP;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Document(collection = "users")
public class User {

    @Id
    private String userId;

    private String email;
    private String userName;
    private String password;
    private String role;
    private String contact;
    private boolean isActive = false;
    private OTP otp;
    private boolean authenticatedForPasswordReset = false;

}
