package com.example.backend.service;

import com.example.backend.entity.ETimeSlot;
import com.example.backend.entity.TimeSlot;
import com.example.backend.entity.User;
import com.example.backend.repository.TimeSlotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TimeSlotService {

    @Autowired
    private TimeSlotRepository timeSlotRepository;


    public TimeSlot createTimeSlot(ETimeSlot slot, User provider) {
        TimeSlot timeSlot = new TimeSlot(slot);
        timeSlot.setProvider(provider);
        return timeSlotRepository.save(timeSlot);
    }

    public ETimeSlot convertStringToETimeSlot (String slot) {
        ETimeSlot createdslot = Enum.valueOf(ETimeSlot.class, slot);
        return createdslot ;
    }

    public TimeSlot addTimeSlotByProviderId (String time , User provider) {
        ETimeSlot eTimeSlot = convertStringToETimeSlot(time);
        TimeSlot timeSlot = new TimeSlot(eTimeSlot);
        timeSlot.setProvider(provider);
        return timeSlotRepository.save(timeSlot);
    }

    public List<TimeSlot> getAll (){
        return timeSlotRepository.findAll();
    }

    public Optional<TimeSlot> getTimeSlotById(Integer id){
        return timeSlotRepository.findById(id);
    }


    public List <TimeSlot> getTimeSlotByServiceId (Long id) { return timeSlotRepository.findByProviderId(id).get() ;}

}
