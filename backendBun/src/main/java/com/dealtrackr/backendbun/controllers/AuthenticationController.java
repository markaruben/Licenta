package com.dealtrackr.backendbun.controllers;

import com.dealtrackr.backendbun.models.ApplicationUser;
import com.dealtrackr.backendbun.models.LoginResponseDTO;
import com.dealtrackr.backendbun.models.RegistrationDTO;
import com.dealtrackr.backendbun.services.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

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
        String token = tokenHeader.substring(7);
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

        Collection<String> role = user.getRoleNames();
        String[] roleArray = role.toArray(new String[0]);

        Map<String, Object> userDetails = new HashMap<>();
        userDetails.put("id", user.getUserId());
        userDetails.put("username", user.getUsername());
        userDetails.put("email", user.getEmail());
        userDetails.put("role", roleArray[0]);


        return ResponseEntity.ok(userDetails);
    }
}
