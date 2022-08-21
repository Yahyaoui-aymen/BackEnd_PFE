package com.example.backend.service;

import com.example.backend.entity.Notification;
import com.example.backend.payload.request.NotificationRequest;
import com.example.backend.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;


    public Notification createNotification (Notification notification) {
        return notificationRepository.save(notification);
    }


    public List<Notification> getByUserId (Long userId){
       return notificationRepository.getByUserId(userId).get();
    }

}
