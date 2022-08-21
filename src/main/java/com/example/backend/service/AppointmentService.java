package com.example.backend.service;


import com.example.backend.entity.*;
import com.example.backend.repository.AppointmentRepository;

import com.example.backend.repository.StatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private StatusRepository statusRepository;

    public List<Appointment> getAllAppointment() {
        return appointmentRepository.findAll();
    }

    public Appointment demandAppointment(User client, Appointment appointment, TimeSlot timeSlot) {

        appointment.setClient(client);
        appointment.setStatus(statusRepository.findById(2).get());
        appointment.setTimeSlot(timeSlot);
        return appointmentRepository.save(appointment);

    }

    public Appointment getAppointmentById(Long id) {
        return appointmentRepository.findById(id).get();
    }

    public Appointment updateAppointmentStatus(Appointment appointment, Status status) {
        appointment.setStatus(status);
        return appointmentRepository.save(appointment);
    }

    public List<Appointment> getAppointmentByProviderId(Long id) {
        return appointmentRepository.findAppointmentByTimeSlot_Provider_Id(id).get();
    }

    public void deleteAppointment(Long id) {
        appointmentRepository.deleteById(id);
    }


    public List<Appointment> getAppointmentByStatusId(Long providerId, Integer statusId) {
        return appointmentRepository.findAppointmentByTimeSlot_Provider_IdAndAndStatusId(providerId, statusId).get();
    }

    public Integer countAppointmentByDateBetweenAndStatusId(Date start_date, Date end_date, Integer statusId) {
        return appointmentRepository.countAppointmentByDateBetweenAndStatusId(start_date, end_date, statusId);
    }

    public HashMap<Integer, Integer> creatStatisticsByStatus(Integer statusId) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        HashMap<Integer, Integer> statistics = new HashMap<>();
        statistics.put(1, countAppointmentByDateBetweenAndStatusId(formatter.parse("01-01-2022"), formatter.parse("01-02-2022"), statusId));
        statistics.put(2, countAppointmentByDateBetweenAndStatusId(formatter.parse("01-02-2022"), formatter.parse("01-03-2022"), statusId));
        statistics.put(3, countAppointmentByDateBetweenAndStatusId(formatter.parse("01-03-2022"), formatter.parse("01-04-2022"), statusId));
        statistics.put(4, countAppointmentByDateBetweenAndStatusId(formatter.parse("01-04-2022"), formatter.parse("01-05-2022"), statusId));
        statistics.put(5, countAppointmentByDateBetweenAndStatusId(formatter.parse("01-05-2022"), formatter.parse("01-06-2022"), statusId));
        statistics.put(6, countAppointmentByDateBetweenAndStatusId(formatter.parse("01-06-2022"), formatter.parse("01-07-2022"), statusId));
        statistics.put(7, countAppointmentByDateBetweenAndStatusId(formatter.parse("01-07-2022"), formatter.parse("01-08-2022"), statusId));
        statistics.put(8, countAppointmentByDateBetweenAndStatusId(formatter.parse("01-08-2022"), formatter.parse("01-09-2022"), statusId));
        statistics.put(9, countAppointmentByDateBetweenAndStatusId(formatter.parse("01-09-2022"), formatter.parse("01-10-2022"), statusId));
        statistics.put(10, countAppointmentByDateBetweenAndStatusId(formatter.parse("01-10-2022"), formatter.parse("01-11-2022"), statusId));
        statistics.put(11, countAppointmentByDateBetweenAndStatusId(formatter.parse("01-11-2022"), formatter.parse("01-12-2022"), statusId));
        statistics.put(12, countAppointmentByDateBetweenAndStatusId(formatter.parse("01-12-2022"), formatter.parse("01-01-2023"), statusId));

        return statistics;
    }

}
