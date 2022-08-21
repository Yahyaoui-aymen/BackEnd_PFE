package com.example.backend.controllers;

import com.example.backend.entity.*;
import com.example.backend.exception.TokenRefreshException;
import com.example.backend.payload.request.*;
import com.example.backend.payload.response.*;
import com.example.backend.regex.EmailValidator;
import com.example.backend.regex.PasswordValidator;
import com.example.backend.regex.PhoneValidator;
import com.example.backend.repository.CategoryRepository;
import com.example.backend.repository.RoleRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.security.jwt.AuthTokenFilter;
import com.example.backend.security.jwt.JwtUtils;
import com.example.backend.security.services.RefreshTokenService;
import com.example.backend.security.services.UserDetailsImpl;

import com.example.backend.security.services.UserDetailsServiceImpl;
import com.example.backend.service.OtpService;
import com.example.backend.service.StorageService;
import com.example.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.Null;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.backend.entity.ERole.ROLE_CLIENT;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AuthTokenFilter authTokenFilter;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private OtpService otpService;

    @Autowired
    private StorageService storageService;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;


    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        if (!userRepository.existsByUsername(loginRequest.getUsername())) {
            return ResponseEntity.badRequest()
                    .body(new Response("Can't signin", "Please check your username ", null));
        }


        User user = userRepository.findByUsername(loginRequest.getUsername()).get();
        if (!encoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return ResponseEntity.badRequest()
                    .body(new Response("Can't signin", "Please check your password ", null));

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

            return ResponseEntity
                    .ok()
                    .body(new Response("You are logged in", null, jwtResponse));
        }

    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
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

        if (strRoles == null) {
            Role userRole = roleRepository.findByName(ROLE_CLIENT)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);

                        break;
                    case "mod":
                        Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(modRole);

                        break;
                    default:
                        Role userRole = roleRepository.findByName(ROLE_CLIENT)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }

        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok(new Response("User registered successfully!", null, user));
    }

    @PostMapping("/refreshtoken")
    public ResponseEntity<?> refreshtoken(@Valid @RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String token = jwtUtils.generateTokenFromUsername(user.getUsername());
                    TokenRefreshResponse tokenRefreshResponse = new TokenRefreshResponse(token, requestRefreshToken);
                    return ResponseEntity.ok(new Response("Your new token", null, tokenRefreshResponse));
                })
                .orElseThrow(() -> new TokenRefreshException(requestRefreshToken, "Refresh token is not in database!"));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(@Valid @RequestBody LogOutRequest logOutRequest) {
        refreshTokenService.deleteByUserId(logOutRequest.getUserId());
        return ResponseEntity.ok(new Response("Log out successful!", null, null));
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getConnectedProfile() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok()
                .body(new Response("Your profile", null, userDetails));

    }

    /*@PutMapping("/updateprofile")
    public ResponseEntity<?> updateConnectedProfile(@Valid @RequestBody UpdateUserRequest updateUserRequest) {
        String loggedUser = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(loggedUser).get();

        if (!userRepository.existsByUsername(updateUserRequest.getUsername()) || updateUserRequest.getUsername() == loggedUser) {
            if (updateUserRequest.getUsername() != null) {
                user.setUsername(updateUserRequest.getUsername());
            }
            if (updateUserRequest.getFirstName() != null) {
                user.setFirstName(updateUserRequest.getFirstName());
            }
            if (updateUserRequest.getLastName() != null) {
                user.setLastName(updateUserRequest.getLastName());
            }
            if (updateUserRequest.getEmail() != null) {
                if (!EmailValidator.isValid(updateUserRequest.getEmail())) {
                    return ValidatorResponse.emailValidatorResponse();
                }
                user.setEmail(updateUserRequest.getEmail());
            }
            if (updateUserRequest.getGovernment() != null) {
                user.setGovernment(updateUserRequest.getGovernment());
            }
            if (updateUserRequest.getCity() != null) {
                user.setCity(updateUserRequest.getCity());
            }
            if (updateUserRequest.getPassword() != null) {
                if (!PasswordValidator.isValid(updateUserRequest.getPassword())) {
                    return ValidatorResponse.passwordValidationResponse();
                }
                user.setPassword(encoder.encode(updateUserRequest.getPassword()));
            }
            if (updateUserRequest.getImageUrl() != null) {
                user.setImageUrl(updateUserRequest.getImageUrl());
            }
        }
        userRepository.save(user);
        return ResponseEntity.ok(new Response("User updated successfully!", null, user));
    }*/


    @PutMapping(value = "/updateprofile", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> updateConnectedProfile(@Nullable @RequestPart("imageFile") MultipartFile imageFile,
                                                    @Nullable @RequestPart("pseudo") String pseudo,
                                                    @Nullable @RequestPart("password") String password) {
        String loggedUser = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(loggedUser).get();

        if (pseudo != null && pseudo.compareTo(user.getUsername()) != 0) {
            if (userRepository.existsByUsername(pseudo)) {
                return ResponseEntity
                        .ok()
                        .body(new Response( "Username is already taken!"  ,"Please use another username", null));
            }
            user.setUsername(pseudo);
        }
        if (password != null) {
            if (!PasswordValidator.isValid(password)) {
                return ValidatorResponse.passwordValidationResponse();
            }
            user.setPassword(encoder.encode(password));
        }
        if (imageFile != null) {
            try {
                Date date = Calendar.getInstance().getTime();
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                String strDate = dateFormat.format(date);
                String replaceString = strDate.replace(":", "-");
                String replaceString2 = replaceString.replace(" ", "_");
                user.setImageUrl(replaceString2 + "_" + imageFile.getOriginalFilename());
                storageService.saveImage(imageFile, replaceString2);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("***************erreur de savegarde de l'image" + e.toString());
            }
        }
        userRepository.save(user);
        UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(user.getUsername());
        return ResponseEntity.ok(new Response("User updated successfully!", null, userDetails));
    }


    @DeleteMapping("/deleteprofile")
    public ResponseEntity<?> deleteProfile(@Valid @RequestBody LoginRequest loginRequest) {
        String loggedUser = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(loggedUser).get();

        if (encoder.matches(loginRequest.getPassword(), currentUser.getPassword())) {
            refreshTokenService.deleteByUserId(currentUser.getId());
            userRepository.deleteUserByUsername(loggedUser);
            return ResponseEntity.ok()
                    .body(new Response("user deleted successfully!", null, loggedUser));
        }

        return ResponseEntity.ok()
                .body(new Response("Please insert your correct password!!", "Password incorrect", null));

    }


    @PostMapping("/updatepassword")
    public ResponseEntity<?> resetPassword(@Valid @RequestParam(name = "id") Long id, @RequestParam(name = "password") String password) {

        User user = userRepository.findById(id).get();
        if (!PasswordValidator.isValid(password))
            return ValidatorResponse.passwordValidationResponse();
        user.setPassword(encoder.encode(password));
        user = userRepository.save(user);
        return ResponseEntity
                .ok()
                .body(new Response("password updated successfull", null, user));

    }


    @PostMapping("/recoverpwd")
    public ResponseEntity<?> recoverPassword(@Valid @RequestBody RecoverPwdRequest recoverPwdRequest) {
        Optional<User> user = userRepository.findByUsernameAndPhoneNumber(recoverPwdRequest.getUsername(), recoverPwdRequest.getPhoneNumber());
        if (!user.isPresent()) {
            return ResponseEntity
                    .ok()
                    .body(new Response(null, "Phone number or username is incorrect", null));
        }
        User currentUser = user.get();
        String otpCode = otpService.sendOTP(recoverPwdRequest.getPhoneNumber());
        currentUser.setOtpCode(otpCode);
        userRepository.save(currentUser);
        return ResponseEntity.ok()
                .body(new Response("We have sent a code on your phone!!", null, user));
    }

    @PostMapping("/validateotp")
    public ResponseEntity<?> validateOtp(@Valid @RequestParam(name = "otpCode") String otpCode, @RequestParam(name = "phoneNumber") String phoneNumber, @RequestParam(name = "username") String username) {
        User user = userRepository.findByUsernameAndPhoneNumber(username, phoneNumber).get();
        String validationOtp = user.getOtpCode();
        if (otpCode.equals(validationOtp)) {
            user.setOtpCode(null);
            userRepository.save(user);
            return ResponseEntity.ok()
                    .body(new Response("your code is correct!!", null, null));
        } else {
            return ResponseEntity.ok()
                    .body(new Response("Validation code", "your code is incorrect!!", null));
        }
    }


    @PostMapping("/presignupadmin")
    public ResponseEntity<?> verifyDataForSignUp(@Valid @RequestBody SignupRequest signUpRequest) {

        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .ok()
                    .body(new Response("Please use another username", "Username is already taken!", null));
        }
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .ok()
                    .body(new Response("Please use another email", "Email is already taken!", null));
        }
        if (!EmailValidator.isValid(signUpRequest.getEmail()))
            return ValidatorResponse.emailValidatorResponse();
        if (!PasswordValidator.isValid(signUpRequest.getPassword()))
            return ValidatorResponse.passwordValidationResponse();
        if (!PhoneValidator.isValid(signUpRequest.getPhoneNumber()))
            return ValidatorResponse.phoneNumberValidationResponse();

        signUpRequest.setOtpCode(otpService.sendOTP(signUpRequest.getPhoneNumber()));

        return ResponseEntity
                .ok()
                .body(new Response("enter your verification code to confirm registration", null, signUpRequest));
    }


    @PostMapping("/resentotp")
    public ResponseEntity<?> resentOtp(@Valid @RequestParam(name = "phoneNumber") String phoneNumber) {

        String otpCode = otpService.sendOTP(phoneNumber);
        return ResponseEntity
                .ok()
                .body(new Response("We have sent a code on your phone!! ", null, otpCode));
    }
}







