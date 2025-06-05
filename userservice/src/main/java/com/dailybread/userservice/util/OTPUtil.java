package com.dailybread.userservice.util;

import java.util.Random;

public class OTPUtil {
    public static OTP generateOtp(int length, int validMinutes) {
        String otp = String.format("%0" + length + "d", new Random().nextInt((int) Math.pow(10, length)));
        long expiryTime = System.currentTimeMillis() + validMinutes * 60 * 1000;
        return new OTP(otp, expiryTime);
    }
}
