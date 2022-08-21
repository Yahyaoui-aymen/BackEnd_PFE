package com.example.backend.controllers;

import com.example.backend.entity.ERole;
import com.example.backend.entity.RefreshToken;
import com.example.backend.entity.Role;
import com.example.backend.entity.User;
import com.example.backend.payload.request.LoginRequest;
import com.example.backend.payload.request.SignupRequest;
import com.example.backend.payload.response.*;
import com.example.backend.regex.EmailValidator;
import com.example.backend.regex.PasswordValidator;
import com.example.backend.regex.PhoneValidator;
import com.example.backend.repository.RoleRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.security.jwt.JwtUtils;
import com.example.backend.security.services.RefreshTokenService;
import com.example.backend.security.services.UserDetailsImpl;
import com.example.backend.service.OtpService;
import com.example.backend.service.PhoneTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.example.backend.entity.ERole.ROLE_CLIENT;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/client")
public class ClientAuthController {

    @Autowired
    private OtpService otpService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PhoneTokenService phoneTokenService;


    @PostMapping("/signin")
    public ResponseEntity<?> authenticateClient(@Valid @RequestBody LoginRequest loginRequest) {

        if (!userRepository.existsByUsernameAndRoles(loginRequest.getUsername(), roleRepository.getById(1))) {
            return ResponseEntity.ok()
                    .body(new Response("Can't signin", "Authentication error :Please check your username", null));
        }


        User user = userRepository.findByUsername(loginRequest.getUsername()).get();
        if (!encoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return ResponseEntity.ok()
                    .body(new Response("Can't signin", "Authentication error: Please check your password", null));

        } else {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            String jwt = jwtUtils.generateJwtToken(userDetails);

            List<String> roles = userDetails.getAuthorities().stream()
                    .map(item -> item.getAuthority())
                    .collect(Collectors.toList());
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

            JwtResponse jwtResponse = new JwtResponse(jwt,
                    refreshToken.getToken(),
                    userDetails.getId(),
                    userDetails.getUsername(),
                    userDetails.getEmail(),
                    roles);

            phoneTokenService.setUserToken(user, loginRequest.getPhoneToken());
            return ResponseEntity
                    .ok()
                    .body( new Response( null , null , jwtResponse));
        }

    }

    @PostMapping("/presignup")
    public ResponseEntity<?> verifyDataForSignUp(@Valid @RequestBody SignupRequest signUpRequest) {

        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .ok()
                    .body(new Response( "Username is already taken!"  ,"Please use another username", null));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .ok()
                    .body(new Response( "Email is already taken!"  ,"Please use another email", null));
        }
        if (!EmailValidator.isValid(signUpRequest.getEmail()))
            return ValidatorResponse.emailValidatorResponse();
        if (!PasswordValidator.isValid(signUpRequest.getPassword()))
            return ValidatorResponse.passwordValidationResponse();
        if(!PhoneValidator.isValid(signUpRequest.getPhoneNumber()))
            return ValidatorResponse.phoneNumberValidationResponse();

        signUpRequest.setOtpCode(otpService.sendOTP(signUpRequest.getPhoneNumber()));

        return ResponseEntity
                .ok()
                .body(new Response( "enter your verification code to confirm registration", null, signUpRequest));
    }


    @PostMapping("/signup")
    public ResponseEntity<?> registerClient(@Valid @RequestBody SignupRequest signUpRequest) {

        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .ok()
                    .body(new Response("Please use another username","Username is already taken!", null));
        }

        Role role = roleRepository.findByName(ROLE_CLIENT).get();
        if (userRepository.existsByRolesAndEmail(role, signUpRequest.getEmail())) {
            return new ResponseEntity<>(new Response("Please use another email","Email is already taken!", null), HttpStatus.OK);
        }
        if (!EmailValidator.isValid(signUpRequest.getEmail()))
            return ValidatorResponse.emailValidatorResponse();
        if (!PasswordValidator.isValid(signUpRequest.getPassword())) {
            return ValidatorResponse.passwordValidationResponse();
        }
        // Create new client's account
        User user = new User(signUpRequest.getFirstName(),
                signUpRequest.getLastName(),
                signUpRequest.getPhoneNumber(),
                signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));

        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName(ERole.ROLE_CLIENT)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        roles.add(userRole);
        user.setRoles(roles);
        userRepository.save(user);

        UserDetailsImpl userDetails = UserDetailsImpl.build(user);

        return ResponseEntity
                .ok()
                .body(new Response("Client registered successfully!", null, userDetails));
    }


}
