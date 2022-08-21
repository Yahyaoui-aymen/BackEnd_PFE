package com.example.backend.controllers;

import com.example.backend.entity.Offer;
import com.example.backend.entity.User;
import com.example.backend.payload.response.EntityResponse;
import com.example.backend.payload.response.Response;
import com.example.backend.repository.OfferRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.service.OfferService;
import com.example.backend.service.StorageService;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

import javax.validation.Valid;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/offer")
public class OfferController {


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OfferRepository offerRepository;

    @Autowired
    private OfferService offerService;

    @Autowired
    private StorageService storageService;


    @GetMapping("all")
    public ResponseEntity<?> getAllOffre() {
        List<Offer> offers = offerService.getAllOffer();
        return ResponseEntity
                .ok()
                .body(new Response("All offres", "", offers));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOfferById(@Valid @PathVariable Integer id) {
        Offer offer = offerService.getById(id).get();
        return ResponseEntity
                .ok()
                .body(new EntityResponse("Offer", "offer", "", offer));
    }

    @PostMapping(value = "/add", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    @PreAuthorize("hasRole('PRESTATAIRE')")
    public ResponseEntity<?> addOffer(@RequestPart("imageFile") MultipartFile imageFile,
                                      @RequestPart("title") String title,
                                      @RequestPart("subtitle") String subtitle,
                                      @RequestPart("description") String description,
                                      @RequestPart("price") String price) {
        String loggedUser = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(loggedUser).get();
        Offer offer = new Offer(title, subtitle, description, price);
        if (imageFile != null) {
            try {
                Date date = Calendar.getInstance().getTime();
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                String strDate = dateFormat.format(date);
                String replaceString = strDate.replace(":", "-");
                String replaceString2 = replaceString.replace(" ", "_");
                offer.setImage(replaceString2 + "_" + imageFile.getOriginalFilename());
                storageService.saveImage(imageFile, replaceString2);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        offer.setUser(user);
        Offer newOffer = offerService.createOffer(offer);
        return ResponseEntity
                .ok()
                .body(new Response(" Offer added successfully ", null, newOffer));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteOffer(@Valid @PathVariable Integer id) {
        Optional<Offer> offer = offerService.getById(id);
        if (!offer.isPresent()) {
            return ResponseEntity
                    .ok()
                    .body(new Response(null, "this offer is not found", null));
        }
        Offer deletedOffer = offer.get();
        offerService.deleteOffer(id);
        return ResponseEntity
                .ok()
                .body(new Response("Offer deleted successfully", null, null));
    }


    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateOffer(@Valid @PathVariable Integer id, @RequestBody Offer newOffer) {
        Offer updatedOffer;
        if (!offerService.existsById(id)) {
            return ResponseEntity
                    .ok()
                    .body(new Response("Offer doesn't exist", null, null));
        }
        updatedOffer = offerService.updateOffer(id, newOffer);
        return ResponseEntity
                .ok()
                .body(new Response("Offer updated successfully", null, updatedOffer));
    }

    @GetMapping("/getbyuser/{prestataireid}")
    public ResponseEntity getByUserID(@Valid @PathVariable("prestataireid") Long prestataireId) {

        Optional<List<Offer>> offers = offerRepository.findByPrestataireId(prestataireId);
        if (offers.get().isEmpty()) {
            return ResponseEntity
                    .ok()
                    .body(new Response(null, "this offer or this service provider is not found", null));
        }
        return ResponseEntity
                .ok()
                .body(new Response("offers posted by " + userRepository.findById(prestataireId).get().getUsername(), null, offers));

    }


    @RequestMapping(value = "/sid", method = RequestMethod.GET,
            produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getImage() throws IOException {

        var imgFile = new ClassPathResource("D:\\media\\photos\\2022-05-16_10-27-20_IMG-0660.jpg");
        byte[] bytes = StreamUtils.copyToByteArray(imgFile.getInputStream());

        return ResponseEntity
                .ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(bytes);
    }


}




