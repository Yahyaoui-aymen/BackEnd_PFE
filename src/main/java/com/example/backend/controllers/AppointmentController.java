package com.example.backend.controllers;


import com.example.backend.entity.*;
import com.example.backend.payload.request.AppointmentRequest;
import com.example.backend.payload.response.EntityResponse;
import com.example.backend.payload.response.MessageResponse;
import com.example.backend.payload.response.Response;
import com.example.backend.payload.response.StatisticsResponse;
import com.example.backend.repository.StatusRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.service.AppointmentService;
import com.example.backend.service.TimeSlotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/appointment")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private StatusRepository statusRepository;

    @Autowired
    private TimeSlotService timeSlotService;

    @Autowired
    private UserRepository userRepository;


    @GetMapping("all")
    public ResponseEntity<?> getAllOffre() {
        List<Appointment> appointments = appointmentService.getAllAppointment();
        return ResponseEntity
                .ok()
                .body(new Response("All appointments", "", appointments));
    }

    @GetMapping("confirmedappointment")
    public ResponseEntity<?> getConfirmedAppointment() {

        String loggedUser = SecurityContextHolder.getContext().getAuthentication().getName();
        User provider = userRepository.findByUsername(loggedUser).get();

        List<Appointment> confirmedAppointments = appointmentService.getAppointmentByStatusId(provider.getId(), 1);

        return ResponseEntity
                .ok()
                .body(new Response("Confirmed appointment", null, confirmedAppointments));
    }

    @GetMapping("confirmedappointment/{providerid}")
    public ResponseEntity<?> getPendingAppointment(@Valid @PathVariable("providerid") Long providerId) {

        List<Appointment> pendingAppointments = appointmentService.getAppointmentByStatusId(providerId, 1);

        return ResponseEntity
                .ok()
                .body(new Response("Confirmed appointment", null, pendingAppointments));
    }

    @PostMapping("/addrdv")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<?> addAppointment(@Valid @RequestBody AppointmentRequest appointmentRequest) {
        String loggedUser = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(loggedUser).get();
        TimeSlot rdvTimeSlot = timeSlotService.getTimeSlotById(appointmentRequest.getTimeSlotId()).get();
        Appointment appointment = new Appointment(appointmentRequest.getDate() , appointmentRequest.getIsOffer());
        Appointment createdRDV = appointmentService.demandAppointment(user, appointment, rdvTimeSlot);
        return ResponseEntity.ok()
                .body(new Response("Appointment Added successfully", null, createdRDV));
    }

    @PostMapping("/confirm/{rdv_id}")
    @PreAuthorize("hasRole('PRESTATAIRE')")
    public ResponseEntity<?> confirmAppointment(@PathVariable("rdv_id") Long rdvId) {

        Appointment appointment = appointmentService.getAppointmentById(rdvId);
        if (appointment.getStatus().getName() != EStatus.STATUS_WAITING) {
            return ResponseEntity.ok()
                    .body(new Response("Appointment already answered", null, null));
        } else {
            appointmentService.updateAppointmentStatus(appointment, statusRepository.findById(1).get());
            return ResponseEntity.ok()
                    .body(new Response("Appointment Confirmed", null, appointment));
        }
    }

    @PostMapping("/reject/{rdv_id}")
    @PreAuthorize("hasRole('PRESTATAIRE')")
    public ResponseEntity<?> rejectAppointment(@PathVariable("rdv_id") Long rdvId) {

        Appointment appointment = appointmentService.getAppointmentById(rdvId);
        if (appointment.getStatus().getName() != EStatus.STATUS_WAITING) {
            return ResponseEntity.ok()
                    .body(new MessageResponse("Appointment", "Appointment already answered", ""));
        } else {
            appointmentService.updateAppointmentStatus(appointment, statusRepository.findById(3).get());
            return ResponseEntity.ok()
                    .body(new Response("Appointment Rejected", null, appointment));
        }
    }


    @PostMapping("/delete/{rdv_id}")
    @PreAuthorize("hasRole('PRESTATAIRE')")
    public void deleteAppointment(@PathVariable("rdv_id") Long rdvId) {

        appointmentService.deleteAppointment(rdvId);

    }

    @GetMapping("/byprovider")
    public ResponseEntity<?> getByProvider() {

        String loggedUser = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(loggedUser).get();

        List<Appointment> appointments = appointmentService.getAppointmentByProviderId(user.getId());

        return ResponseEntity
                .ok()
                .body(new Response("Appointments", null, appointments));
    }


    @GetMapping("/statistics")
    public ResponseEntity<?> getappointmentstatistics() throws ParseException {

        HashMap<Integer, Integer> approuvedStatistics = appointmentService.creatStatisticsByStatus(1);
        HashMap<Integer, Integer> rejectedStatistics = appointmentService.creatStatisticsByStatus(3);
        HashMap<Integer, Integer> waitingStatistics = appointmentService.creatStatisticsByStatus(2);

        StatisticsResponse statisticsResponse = new StatisticsResponse(approuvedStatistics , rejectedStatistics , waitingStatistics);
        System.out.println(statisticsResponse.toString());

        return ResponseEntity
                .ok()
                .body(new Response("Appointments", null, statisticsResponse));
    }

}
