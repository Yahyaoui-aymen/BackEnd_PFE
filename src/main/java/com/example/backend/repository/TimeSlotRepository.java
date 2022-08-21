package com.example.backend.repository;

import com.example.backend.entity.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface TimeSlotRepository extends JpaRepository<TimeSlot, Integer> {

    Optional<List<TimeSlot>> findByProviderId(Long providerID);

}
