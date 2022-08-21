package com.example.backend.service;

import com.example.backend.payload.request.OtpRequest;
import com.example.backend.payload.response.OtpResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

@Service
public class OtpService {

    @Value("${login.app.smssender}")
    private String smsSender;

    static RestTemplate restTemplate = new RestTemplate();

    @Value("${login.app.sender}")
    private String sender;

    @Value("${login.app.apikey}")
    private String apikey;


    public String sendOTP(String phoneNumber) {

        String otpCode = generateOTP();
        OtpRequest.Message message = new OtpRequest.Message();
        message.setNumber(phoneNumber);
        message.setMessage("your validation code is " + otpCode);
        OtpRequest otpRequest = new OtpRequest();
        otpRequest.setApikey(apikey);
        otpRequest.setSender(sender);
        otpRequest.getMessages().add(message);
        ResponseEntity<OtpResponse> Result = restTemplate.postForEntity(smsSender, otpRequest, OtpResponse.class);
        return otpCode;
    }


    // Function that generates a 6-digit code
    public String generateOTP() {
        return new DecimalFormat("000000")
                .format(new Random().nextInt(999999));
    }
}
