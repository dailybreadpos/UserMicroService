package com.dailybread.userservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddRequest {
    private String userName;
    private String email;
}
