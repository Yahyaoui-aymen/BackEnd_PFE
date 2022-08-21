package com.example.backend.service;

import com.example.backend.entity.User;
import com.example.backend.repository.UserRepository;
import com.example.backend.security.services.UserDetailsImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;



 /*   public User getJson(String user, MultipartFile file) {
        User userJson = new User();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            userJson = objectMapper.readValue(user, User.class);
        } catch (IOException err) {
            System.out.printf("Error", err.toString());
        }
        String fileName = fileStorageService.storeFile(file);
        userJson.setImageUrl(fileName);

        return userJson;

    }*/

    public List<User> loadUserByCategory(Integer categoryId) {
        List<User> users = userRepository.findByCategoryId(categoryId)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with category: " + categoryId));
        return users;
    }

    public User getProviderById (Long providerId){
        User user = userRepository.findById(providerId).get();
        return user;
    }

    public User getUserByUsername (String username){
        User user = userRepository.findByUsername(username).get();
        return user;
    }

}
