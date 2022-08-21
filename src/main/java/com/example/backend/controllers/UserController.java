package com.example.backend.controllers;

import com.example.backend.entity.Role;
import com.example.backend.entity.User;
import com.example.backend.payload.request.SignupRequest;
import com.example.backend.payload.response.CountResponse;
import com.example.backend.payload.response.Response;
import com.example.backend.repository.RoleRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.security.services.RefreshTokenService;
import com.example.backend.service.OfferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.example.backend.entity.ERole.*;

@RestController
@CrossOrigin("http://localhost:4200")
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OfferService offerService;

    @GetMapping("/all")
   // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAll() {

        List<User> data = userRepository.findAll();
        return ResponseEntity
                .ok()
                .body(new Response("this is all users", null, data));
    }

    @GetMapping("/allmoderator")
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllMofderators() {
        Role userRole = roleRepository.findByName(ROLE_MODERATOR).get();
        List<User> data = userRepository.findByRoles(userRole).get();
        return ResponseEntity
                .ok()
                .body(new Response("this is all moderators", null, data));
    }

    @GetMapping("/allclient")
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllClients() {
        Role userRole = roleRepository.findByName(ROLE_CLIENT).get();
        List<User> data = userRepository.findByRoles(userRole).get();
        return ResponseEntity
                .ok()
                .body(new Response("this is all clients", null, data));
    }

    @GetMapping("/allprovider")
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllProviders() {
        Role userRole = roleRepository.findByName(ROLE_PRESTATAIRE).get();
        List<User> data = userRepository.findByRoles(userRole).get();
        return ResponseEntity
                .ok()
                .body(new Response("this is all providers", null, data));
    }


    @GetMapping("/userstatistics")
    public ResponseEntity <?> getAllUsers(){
        Role provider = roleRepository.findByName(ROLE_PRESTATAIRE).get();
        Role client =  roleRepository.findByName(ROLE_CLIENT).get();
        Role moderator = roleRepository.findByName(ROLE_MODERATOR).get();

        List<User> providers = userRepository.findByRoles(provider).get();
        List<User> clients = userRepository.findByRoles(client).get();
        List<User> moderators = userRepository.findByRoles(moderator).get();

        CountResponse countResponse = new CountResponse(clients.size(),providers.size(),moderators.size());

        return ResponseEntity
                .ok()
                .body( new Response("All users by roles" , "" ,countResponse) );
    }

    @GetMapping("/getbyoffer/{offerid}")
    public ResponseEntity<?> getByOfferId(@PathVariable("offerid") Integer offerId) {
        User user = userRepository.findByOffersId(offerId).get();
        return ResponseEntity.ok()
                .body(new Response("User Having the offer" + offerId, null, user));
    }

    @GetMapping("getbycategory/{categoryid}")
    public ResponseEntity<?> getByCategory(@PathVariable("categoryid") Integer categoryId) {
        List<User> users = userRepository.findByCategoryId(categoryId).get();
        return ResponseEntity.ok()
                .body(new Response("User Having offers related to" + categoryId, null, users));
    }

    @PostMapping("addmoderator")
    public ResponseEntity<?> addModerator(@Valid @RequestBody SignupRequest signUpRequest) {
        // Create new user's account
        User user = new User(signUpRequest.getFirstName(),
                signUpRequest.getLastName(),
                signUpRequest.getPhoneNumber(),
                signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword())

        );

        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        Role userRole = roleRepository.findByName(ROLE_MODERATOR)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        roles.add(userRole);

        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok(new Response("User registered successfully!", null, user));
    }

    @PostMapping("updatemoderator/{moderatorid}")
    public ResponseEntity<?> updateModerator(@Valid @RequestBody SignupRequest signUpRequest, @PathVariable("moderatorid") Long id) {
        // Create new user's account
        User user = userRepository.findById(id).get();
                user.setFirstName(signUpRequest.getFirstName());
                user.setLastName(signUpRequest.getLastName());
                user.setPhoneNumber(signUpRequest.getPhoneNumber());
                user.setUsername(signUpRequest.getUsername());
                user.setEmail(signUpRequest.getEmail());
                user.setPassword(encoder.encode(signUpRequest.getPassword()));
        userRepository.save(user);

        return ResponseEntity.ok(new Response("User modified successfully!", null, user));
    }

    @DeleteMapping("deleteuser/{userid}")
    public ResponseEntity<?> deleteUser(@PathVariable("userid") Long id) {
        userRepository.deleteById(id);
        return ResponseEntity.ok(new Response("User deleted successfully!", null, null));
    }

    @DeleteMapping("deleteprovider/{providerid}")
    public ResponseEntity<?> deleteProvider(@PathVariable("providerid") Long id) {
        refreshTokenService.deleteByUserId(id);
        userRepository.deleteById(id);
        return ResponseEntity.ok(new Response("Provider deleted successfully!", null, null));
    }

    @DeleteMapping("deleteclient/{clientid}")
    public ResponseEntity<?> deleteClient(@PathVariable("clientid") Long id) {
        refreshTokenService.deleteByUserId(id);
        userRepository.deleteById(id);
        return ResponseEntity.ok(new Response("Client deleted successfully!", null, null));
    }

}
