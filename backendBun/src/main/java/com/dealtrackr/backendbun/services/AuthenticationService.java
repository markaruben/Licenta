package com.dealtrackr.backendbun.services;

import com.dealtrackr.backendbun.exceptions.AuthenticationFailedException;
import com.dealtrackr.backendbun.exceptions.UserNotFoundException;
import com.dealtrackr.backendbun.models.ApplicationUser;
import com.dealtrackr.backendbun.models.LoginResponseDTO;
import com.dealtrackr.backendbun.models.RegistrationDTO;
import com.dealtrackr.backendbun.models.Role;
import com.dealtrackr.backendbun.repository.RoleRepository;
import com.dealtrackr.backendbun.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
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

    @Autowired
    private JwtDecoder jwtDecoder;

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
            throw new UserNotFoundException("User doesn't exist");
        }
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            String token = tokenService.generateJwt(auth);
            return new LoginResponseDTO(userRepository.findByUsername(username).get(), token);
        } catch (AuthenticationException e) {
            throw new AuthenticationFailedException("Authentication Failed!");
        }
    }

    public String getUsernameFromToken(String token) {
        var jwt = jwtDecoder.decode(token);
        return jwt.getClaimAsString("sub");
    }

    public Optional<ApplicationUser> loadUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

}
