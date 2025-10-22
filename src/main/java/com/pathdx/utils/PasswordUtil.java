package com.pathdx.utils;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class PasswordUtil {

    public String generateTemporaryPasswordforLogin() {
        Random rnd = new Random();
        int number = rnd.nextInt(999999);
        return String.format("%06d", number);
    }

}
