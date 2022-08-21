package com.example.backend.controllers;


import com.example.backend.payload.response.Response;
import com.example.backend.service.PhoneTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/phonetoken")
public class PhoneTokenController {

    @Autowired
    private PhoneTokenService phoneTokenService;



    @DeleteMapping("/delete")
    public void delete (@Valid @RequestParam(name = "phoneToken") String token) {
        phoneTokenService.deletePhoneToken(token);
    }

}
