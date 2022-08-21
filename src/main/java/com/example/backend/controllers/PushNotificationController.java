package com.example.backend.controllers;

import com.example.backend.payload.request.PushNotificationRequest;
import com.example.backend.payload.response.PushNotificationResponse;
import com.example.backend.service.PushNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;


import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@RequestMapping("/api/notification")

@RestController
public class PushNotificationController {


    @Autowired
    private PushNotificationService pushNotificationService;

    /*public PushNotificationController(PushNotificationService pushNotificationService) {
        this.pushNotificationService = pushNotificationService;
    }*/

    @PostMapping("/send")
    public void sendTokenNotification(@Nullable @RequestParam(name = "phoneTokens") List<String> tokens,
                                      @RequestParam(name = "title") String title,
                                      @RequestParam(name = "message") String message) {
        tokens.stream().forEach(
                token -> {
                    pushNotificationService.sendPushNotificationToToken(new
                            PushNotificationRequest(title, message, null, token));
                });
    }
}
