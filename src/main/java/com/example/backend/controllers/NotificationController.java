package com.example.backend.controllers;


import com.example.backend.entity.Notification;
import com.example.backend.entity.User;
import com.example.backend.payload.request.NotificationRequest;
import com.example.backend.payload.response.Response;
import com.example.backend.repository.UserRepository;
import com.example.backend.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/notification")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserRepository userRepository;


    @PostMapping("/add")
    public ResponseEntity<?> addNotification(@Valid @RequestBody NotificationRequest notificationRequest) {

        User user = userRepository.findById(notificationRequest.getUserId()).get();

        Notification notification = new Notification(notificationRequest.getTitle(), notificationRequest.getMessage());
        notification.setUser(user);

        Notification createdNotification = notificationService.createNotification(notification);


        return ResponseEntity
                .ok()
                .body(new Response("created successfully", null, createdNotification));
    }

    @GetMapping("/getbyuser")
    public ResponseEntity<?> getByUserId() {

        String loggedUser = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(loggedUser).get();

        List<Notification> notifications = notificationService.getByUserId(user.getId());

        if (notifications.isEmpty()) {
            return ResponseEntity
                    .ok()
                    .body(new Response(null, "You have no notifications", null));
        }
        return ResponseEntity
                .ok()
                .body(new Response("user's notifications", null, notifications));

    }

}
