package com.fashionfinds.backendbun.services;

import com.fashionfinds.backendbun.models.ApplicationUser;
import com.fashionfinds.backendbun.models.LoginResponseDTO;
import com.fashionfinds.backendbun.models.RegistrationDTO;
import com.fashionfinds.backendbun.models.Role;
import com.fashionfinds.backendbun.repository.RoleRepository;
import com.fashionfinds.backendbun.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class AuthenticationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    public ApplicationUser registerUser(RegistrationDTO body) {

        String encodedPassword = passwordEncoder.encode(body.getPassword());
        Role userRole = roleRepository.findByAuthority("USER").get();

        Set<Role> authorities = new HashSet<>();

        authorities.add(userRole);
        return userRepository.save(new ApplicationUser(0, body.getUsername(), body.getEmail(), "{bcrypt}" + encodedPassword, authorities));
    }

    public LoginResponseDTO loginUser(String username, String password) {
        Optional<ApplicationUser> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            // Dacă utilizatorul nu există, returnează un răspuns specific
            return new LoginResponseDTO(null, "User doesn't exist");
        }
        try {

            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            String token = tokenService.generateJwt(auth);
            return new LoginResponseDTO(userRepository.findByUsername(username).get(), token);
        } catch (AuthenticationException e) {
            return new LoginResponseDTO(null, "Authentication Failed!");
        }
    }
}
