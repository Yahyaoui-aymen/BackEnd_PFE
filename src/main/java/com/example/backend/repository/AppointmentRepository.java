package com.example.backend.repository;

import com.example.backend.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment , Long> {


    Optional <List<Appointment>> findAppointmentByTimeSlot_Provider_Id (Long providerId);

    Optional<List<Appointment>> findAppointmentByTimeSlot_Provider_IdAndAndStatusId(Long providerId , Integer statusId);


    Integer countAppointmentByDateBetweenAndStatusId(Date start_date, Date end_date , Integer statusId);

}
