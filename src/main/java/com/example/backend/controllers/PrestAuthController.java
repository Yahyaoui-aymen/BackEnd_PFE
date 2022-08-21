package com.example.backend.controllers;

import com.example.backend.entity.*;
import com.example.backend.payload.request.LoginRequest;
import com.example.backend.payload.request.SignupRequest;
import com.example.backend.payload.response.*;
import com.example.backend.regex.EmailValidator;
import com.example.backend.regex.PasswordValidator;
import com.example.backend.regex.PhoneValidator;
import com.example.backend.repository.CategoryRepository;
import com.example.backend.repository.RoleRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.security.jwt.JwtUtils;
import com.example.backend.security.services.RefreshTokenService;
import com.example.backend.security.services.UserDetailsImpl;
import com.example.backend.service.OtpService;
import com.example.backend.service.PhoneTokenService;
import com.example.backend.service.TimeSlotService;
import com.example.backend.service.UserService;
import com.sun.istack.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.websocket.server.PathParam;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.backend.entity.ERole.ROLE_PRESTATAIRE;

@RestController
@RequestMapping("/api/prestataire")
public class PrestAuthController {

    @Autowired
    private AuthenticationManager authenticationManager;


    @Autowired
    private TimeSlotService timeSlotService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private OtpService otpService;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private PhoneTokenService phoneTokenService;

    @PostMapping(value = "/signup")
// consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> registerPrest(@Valid @RequestBody SignupRequest signUpRequest) {


        ECategory categoryName1 = ECategory.valueOf(signUpRequest.getCategory());
        Category category = categoryRepository.findByName(categoryName1).get();

        // Create new client's account
        User NewUser = new User(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()),
                signUpRequest.getFirstName(),
                signUpRequest.getLastName(),
                signUpRequest.getPhoneNumber(),
                signUpRequest.getGovernment(),
                signUpRequest.getCity(),
                category,
                signUpRequest.getImageUrl()
        );

        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName(ROLE_PRESTATAIRE)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        roles.add(userRole);
        NewUser.setRoles(roles);
        User providerRegistered = userRepository.save(NewUser);

        List<String> timeSlots = Arrays.asList("NINE","TEN","ELEVEN","TWELVE","FOURTEEN","FIFTEEN","SIXTEEN","SEVENTEEN","EIGHTEEN");
        timeSlots.stream()
                .forEach(item -> timeSlotService.addTimeSlotByProviderId(item, providerRegistered));

        return ResponseEntity.ok(new Response("Prestataire registered successfully!", null, NewUser));
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        if (!userRepository.existsByUsernameAndRoles(loginRequest.getUsername(), roleRepository.getById(2))) {
            return ResponseEntity.ok()
                    .body(new Response("Can't signin", "Authentication error :Please check your username", null));
        }

        User user = userRepository.findByUsername(loginRequest.getUsername()).get();
        if (!encoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return ResponseEntity.ok()
                    .body(new Response("Can't signin", "Authentication error: Please check your password", null));

        }

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
        return ResponseEntity.ok(new Response("Signed in successfully", null, jwtResponse));


    }

 /* @PostMapping(value = "/presignup", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> verifyDataForSignUp(@Valid @RequestPart("user") String user, @RequestPart("file") MultipartFile file, @RequestPart("category") String categoryName) {

        User userJson = userService.getJson(user, file);

        if (userRepository.existsByUsername(userJson.getUsername())) {
            return ResponseEntity
                    .ok()
                    .body(new Response(null, "Username is already taken!  Please use another username", null));
        }

        Role role = roleRepository.findByName(ROLE_PRESTATAIRE).get();
        if (userRepository.existsByRolesAndEmail(role, userJson.getEmail())) {

            return ResponseEntity
                    .ok()
                    .body(new Response(null, "Email is already taken!  Please use another email", null));

        }
        if (!EmailValidator.isValid(userJson.getEmail()))
            return ValidatorResponse.emailValidatorResponse();
        if (!PasswordValidator.isValid(userJson.getPassword()))
            return ValidatorResponse.passwordValidationResponse();
        if(!PhoneValidator.isValid(userJson.getPhoneNumber()))
            return ValidatorResponse.phoneNumberValidationResponse();



        //CREATE SIGNUP REQUEST
        SignupRequest signupRequest = new SignupRequest(userJson.getUsername(),
                userJson.getEmail(),
                categoryName,
                userJson.getPassword(),
                userJson.getFirstName(),
                userJson.getLastName(),
                userJson.getGovernment(),
                userJson.getCity(),
                userJson.getPhoneNumber(),
                userJson.getImageUrl()
        );


        signupRequest.setOtpCode(otpService.sendOTP(signupRequest.getPhoneNumber()));
        return ResponseEntity
                .ok()
                .body(new Response( "enter your verification code to confirm registration", "", signupRequest));
    }*/

    @PostMapping("/presignup")
    public ResponseEntity<?> verifyDataForSignUp(@Valid @RequestBody SignupRequest signUpRequest) {

        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .ok()
                    .body(new Response("Username is already taken!", "Please use another username", null));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .ok()
                    .body(new Response("Email is already taken!", "Please use another email", null));
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


    @PostMapping("/getbycategory")
    public ResponseEntity<?> getByCategoryGovernmentCity(@Valid @Nullable @RequestParam(name = "categoryid") Integer categoryId,
                                                         @Nullable @RequestParam(name = "government") String government,
                                                         @Nullable @RequestParam(name = "city") String city) {
        if (government.isEmpty() && city.isEmpty()) {
            Optional<List<User>> users = userRepository.findByCategoryId(categoryId);
            return ResponseEntity
                    .ok()
                    .body(new Response("all users have category " + categoryId, null, users));
        } else if (city.isEmpty()) {
            Optional<List<User>> users = userRepository.findByCategoryIdAndGovernment(categoryId, government);
            return ResponseEntity
                    .ok()
                    .body(new Response("all users have category " + categoryId + " and their lacation in " + government, null, users));
        } else {
            Optional<List<User>> users = userRepository.findByCategoryIdAndGovernmentAndCity(categoryId, government, city);
            return ResponseEntity
                    .ok()
                    .body(new Response("all users", null, users));
        }

    }
}
