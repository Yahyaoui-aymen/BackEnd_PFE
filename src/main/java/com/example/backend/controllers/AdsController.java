package com.example.backend.controllers;

import com.example.backend.entity.Ads;
import com.example.backend.payload.request.AdsRequest;
import com.example.backend.payload.response.EntityResponse;
import com.example.backend.payload.response.MessageResponse;
import com.example.backend.payload.response.Response;
import com.example.backend.service.AdsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/ads")
public class AdsController {

    @Autowired
    private AdsService adsService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllAds() {
        List<Ads> ads = adsService.getAllAds();
        return ResponseEntity.ok()
                .body(new Response("All ads", "", ads));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAdsById(@PathVariable("id") Long id) {
        Ads ad = adsService.getAdById(id);
        return ResponseEntity.ok()
                .body(new Response("Ad" + id, "", ad));
    }

    @PostMapping("/add")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addAds(@Valid @RequestBody Ads ad) {
        Ads createdAd = adsService.createAds(ad);
        return ResponseEntity.ok()
                .body(new Response("Advertisement Added successfully", "", createdAd));
    }

    @PutMapping("update/{id}")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateAds(@PathVariable("id") Long id, @Valid @RequestBody AdsRequest changes) {
        Ads updatedAd = adsService.updateAd(id, changes);
        return ResponseEntity.ok()
                .body(new Response("Advertisement updated successfully", "", updatedAd));
    }

    @DeleteMapping("delete/{id}")
   //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> removeAds(@PathVariable("id") Long id) {
        Ads ad = adsService.getAdById(id);
        adsService.deleteAds(id);
        return ResponseEntity.ok()
                .body(new Response("Advertisement removed successfully", "", ad));
    }

}
