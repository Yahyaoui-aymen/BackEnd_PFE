package com.example.backend.service;


import com.example.backend.entity.Offer;
import com.example.backend.repository.OfferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OfferService {

    @Autowired
    private OfferRepository offerRepository;

    public Offer createOffer(Offer offer) {
        return offerRepository.save(offer);
    }

    public List<Offer> getAllOffer() {
        return offerRepository.findAll();
    }

    public Optional<Offer> getById(Integer id) {
        return offerRepository.findById(id);
    }

    public void deleteOffer(Integer id) {
        offerRepository.deleteById(id);
    }

    public Offer updateOffer(Integer id, Offer newOffer) {
        Offer currentOffer = offerRepository.findById(id).get();
        if (newOffer.getTitle() != null) {
            currentOffer.setTitle(newOffer.getTitle());
        }
        if (newOffer.getDescription() != null) {
            currentOffer.setDescription(newOffer.getDescription());
        }
        if (newOffer.getPrice() != null) {
            currentOffer.setPrice(newOffer.getPrice());
        }
        return offerRepository.save(currentOffer);

    }

    public boolean existsById(Integer id) { return offerRepository.existsById(id); }

}
