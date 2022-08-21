package com.example.backend.regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhoneValidator {


    private static final String PHONE_NUMBER_PATTERN =
            "((\\+|00)216)?([2579][0-9]{7}|(3[012]|4[01]|8[0128])[0-9]{6}|42[16][0-9]{5})";

    private static final Pattern pattern = Pattern.compile(PHONE_NUMBER_PATTERN);

    public static boolean isValid(final String phoneNumber) {
        Matcher matcher = pattern.matcher(phoneNumber);
        return matcher.matches();
    }
}
