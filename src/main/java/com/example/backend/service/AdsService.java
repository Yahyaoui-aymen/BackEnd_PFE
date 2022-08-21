package com.example.backend.service;

import com.example.backend.entity.Ads;
import com.example.backend.payload.request.AdsRequest;
import com.example.backend.repository.AdsRepository;
import com.example.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AdsService {

    @Autowired
    private AdsRepository adsRepository;

    public Ads createAds(Ads advertisement) {
        return adsRepository.save(advertisement);
    }

    public List<Ads> getAllAds() {
        return adsRepository.findAll();
    }

    public Ads getAdById(Long id) {
        return adsRepository.findById(id).get();
    }

    public Ads updateAd(Long id , AdsRequest changes) {
        Ads currentAd = adsRepository.getById(id);
        if(changes.getTitle()!=null) {
            currentAd.setTitle(changes.getTitle());
        }
        if(changes.getDescription()!=null) {
            currentAd.setDescription(changes.getDescription());
        }
        if(changes.getMedia()!=null) {
            currentAd.setMedia(changes.getMedia());
        }
        return adsRepository.save(currentAd);
    }

    public void deleteAds(Long id) {
        adsRepository.deleteById(id);
    }

}
