package com.fashionfinds.backendbun.controllers;

import com.fashionfinds.backendbun.models.ApplicationUser;
import com.fashionfinds.backendbun.models.LoginResponseDTO;
import com.fashionfinds.backendbun.models.RegistrationDTO;
import com.fashionfinds.backendbun.services.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@CrossOrigin("*")
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/register")
    public ApplicationUser registerUser(@RequestBody RegistrationDTO body) {
        return authenticationService.registerUser(body);
    }

    @PostMapping("/login")
    public LoginResponseDTO loginUser(@RequestBody RegistrationDTO body) {
        return authenticationService.loginUser(body.getUsername(), body.getPassword());
    }

    @GetMapping("/user-details")
    public ResponseEntity<?> getUserDetails(@RequestHeader(name = "Authorization") String tokenHeader, HttpServletRequest request) {
        if (tokenHeader == null || !tokenHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("Invalid JWT Token in request headers");
        }
        String token = tokenHeader.substring(7); // Remove 'Bearer ' from the token
        String username = authenticationService.getUsernameFromToken(token);
        if (username == null) {
            return ResponseEntity.badRequest().body("Invalid JWT Token");
        }

        ApplicationUser user;
        Optional<ApplicationUser> userIDK = authenticationService.loadUserByUsername(username);
        if (userIDK.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            user = userIDK.get();
        }

        Map<String, Object> userDetails = new HashMap<>();
        userDetails.put("id", user.getUserId());
        userDetails.put("username", user.getUsername());
        userDetails.put("email", user.getEmail());

        return ResponseEntity.ok(userDetails);
    }
}
