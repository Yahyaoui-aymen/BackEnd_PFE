package com.example.backend.payload.response;

import org.springframework.http.ResponseEntity;

public class ValidatorResponse {

    public static ResponseEntity passwordValidationResponse() {
        return ResponseEntity
                .ok()
                .body(new Response("Password must contain at least one digit [0-9]." +
                        "Password must contain at least one lowercase Latin character [a-z]." +
                        "Password must contain at least one special character like ! @ # & ( )." +
                        "Password must contain a length of at least 8 characters and a maximum of 20 characters.", "Invalid password", null));


    }

    public static ResponseEntity emailValidatorResponse() {
        return ResponseEntity
                .ok()
                .body(new Response("Email format should be like :" +
                        "Uppercase and lowercase letters in Latin (A-Z, a-z)" +
                        "Digits from 0 to 9." +
                        "A hyphen (-)" +
                        "A period (.) (used to identify a sub-domain; for example, username@domain.com)", "Invalid email", null));


    }

    public static ResponseEntity phoneNumberValidationResponse() {
        return ResponseEntity
                .ok()
                .body(new Response("Phone number should be like : (+/00)216XXXXXXXX ", "Invalid phone number",null));
    }

}
