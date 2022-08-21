package com.example.backend.payload.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import java.util.Set;

@Getter
@Setter

public class SignupRequest {
    @NotBlank
    @Size(min = 3, max = 20)
    private String username;

    @NotBlank
    @Size(max = 50)
    private String email;

    private Set<String> role;

    private String category;

    @NotBlank
    private String password;

    private String firstName;

    private String lastName;

    private String government;
    private String city;

    @NotBlank
    private String phoneNumber;

    private String imageUrl;

    private String otpCode;

    public SignupRequest(String username,
                         String email, String category,
                         String password, String firstName,
                         String lastName, String government,
                         String city, String phoneNumber, String imageUrl) {
        this.username = username;
        this.email = email;
        this.category = category;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.government = government;
        this.city = city;
        this.phoneNumber = phoneNumber;
        this.imageUrl = imageUrl;
    }
}
