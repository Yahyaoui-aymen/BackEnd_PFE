package com.example.backend.controllers;


import com.example.backend.entity.TimeSlot;
import com.example.backend.entity.User;
import com.example.backend.payload.response.Response;
import com.example.backend.repository.UserRepository;
import com.example.backend.security.services.UserDetailsImpl;
import com.example.backend.service.TimeSlotService;
import com.example.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/timeslot")
public class TimeSlotController {


    @Autowired
    private TimeSlotService timeSlotService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;


    @PostMapping("/add")
    @PreAuthorize("hasRole('PRESTATAIRE')")
    public ResponseEntity<?> addTimeSlotByProviderId(@Valid @RequestParam List<String> timeSlots) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User loggedProvider = userService.getUserByUsername(username);
        List<TimeSlot> timeSlotList = timeSlots.stream()
                .map(item -> timeSlotService.addTimeSlotByProviderId(item, loggedProvider))
                .collect(Collectors.toList());
        return ResponseEntity
                .ok()
                .body(new Response("TimeSlots added to service " + loggedProvider.getCategory().getName(), null, timeSlotList));

    }

    @GetMapping("byprovider")
    public ResponseEntity<?> getTimeSlotByProvider() {

        String loggedUser = SecurityContextHolder.getContext().getAuthentication().getName();
        User provider = userRepository.findByUsername(loggedUser).get();

        List<TimeSlot> timeSlots = timeSlotService.getTimeSlotByServiceId(provider.getId());
        return ResponseEntity
                .ok()
                .body(new Response(null, null, timeSlots));
    }


    @GetMapping("byprovider/{provider_id}")
    public ResponseEntity<?> getTimeSlotByProvider(@Valid @PathVariable("provider_id") Long  providerId) {

        List<TimeSlot> timeSlots = timeSlotService.getTimeSlotByServiceId(providerId);
        return ResponseEntity
                .ok()
                .body(new Response(null, null, timeSlots));
    }



}
