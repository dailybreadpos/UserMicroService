package com.dailybread.userservice.util;

public class OTP {

    private String otp;
    private Long expiryTime;

    public OTP(String otp, Long expiryTime) {
        this.otp = otp;
        this.expiryTime = expiryTime;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > expiryTime;
    }

    public String getOtp() {
        return otp;
    }

    public Long getExpiryTime() {
        return expiryTime;
    }
}
