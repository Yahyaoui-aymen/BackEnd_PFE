package com.example.backend.service;


import com.example.backend.entity.PhoneToken;
import com.example.backend.entity.User;
import com.example.backend.repository.PhoneTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PhoneTokenService {

    @Autowired
    private PhoneTokenRepository phoneTokenRepository;

    public PhoneToken createPhoneToken(PhoneToken phoneToken) {
        return phoneTokenRepository.save(phoneToken);
    }

    public void deletePhoneToken(String token) {
       PhoneToken phoneToken =  phoneTokenRepository.findByToken(token).get();
        phoneTokenRepository.deleteById(phoneToken.getId());
    }

    public Optional<List<PhoneToken>> getByUserId(Long userId) {
        Optional<List<PhoneToken>> phoneTokens = phoneTokenRepository.findByUser_Id(userId);
        return phoneTokens;
    }

    public Boolean checkExistance(String token) {
        return phoneTokenRepository.existsByToken(token);
    }

    public void setUserToken(User user, String token) {
        if (phoneTokenRepository.existsByToken(token)) {
            PhoneToken phoneToken = phoneTokenRepository.findByToken(token).get();
            phoneToken.setUser(user);
            phoneTokenRepository.save(phoneToken);
        } else {
            PhoneToken phoneToken = new PhoneToken(null, token, user);
            phoneTokenRepository.save(phoneToken);
        }
    }
}
