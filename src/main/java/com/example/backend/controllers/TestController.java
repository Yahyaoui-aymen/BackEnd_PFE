package com.example.backend.controllers;

import com.example.backend.entity.ETimeSlot;
import com.example.backend.entity.User;
import com.example.backend.payload.response.EntityResponse;
import com.example.backend.repository.UserRepository;
import com.example.backend.service.TimeSlotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/test")
public class TestController {


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TimeSlotService timeSlotService;

    @GetMapping("/all")
    public String allAccess() {
        return "public Content";

    }

    @GetMapping("/client")
    @PreAuthorize("hasRole('Client') or hasRole('PRESTATAIRE') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public String userAccess() {
        return "User Content.";
    }

    @GetMapping("/prestataire")
    @PreAuthorize("hasRole('PRESTATAIRE') or hasRole('CLIENT') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public String prestAccess() {
        return "Prestataire Content.";
    }

    @GetMapping("/mod")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public String moderatorAccess() {
        return "Moderator Board.";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminAccess() {
        return "Admin Board.";
    }


    @PostMapping("/add/{id_provider}")
    public ResponseEntity <?> addTimeSlot (@Valid @RequestParam List<String> timeSlots ,
            @PathVariable("id_provider") Long idProvider ){

        String test = "NINE";
        ETimeSlot ttttt = timeSlotService.convertStringToETimeSlot(test);
        User provider = userRepository.findById(idProvider).get();
        timeSlotService.createTimeSlot(ttttt ,provider);

        System.out.println(Enum.valueOf(ETimeSlot.class, test));
      /*  Serv service = new Serv();
        timeSlotService.createTimeSlot(test , service );
        List<String> items = timeSlots.stream()
                .map(item -> item.toUpperCase())
                .map(item -> item + "ok")
                .collect(Collectors.toList());*/
        return ResponseEntity
                .ok()
                .body(new EntityResponse("","","" ,ttttt));

    }


  /*  @GetMapping("/files/{filename:.+}")
    public byte[] getFile(@PathVariable String filename) {
        Path resource = fileStorageService.loadFileAsRessource(filename);
        byte[] bytes = new byte[0];
        try {
            bytes = Files.readAllBytes(resource);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;

    }*/
}
