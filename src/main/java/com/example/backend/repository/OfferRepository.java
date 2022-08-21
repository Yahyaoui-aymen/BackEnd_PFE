package com.example.backend.repository;

import com.example.backend.entity.Offer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OfferRepository extends JpaRepository<Offer, Integer> {


    Optional <List <Offer>> findByPrestataireId (Long userId);



}
