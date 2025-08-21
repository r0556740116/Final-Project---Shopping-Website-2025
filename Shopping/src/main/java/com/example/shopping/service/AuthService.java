package com.example.shopping.service;

import com.example.shopping.model.AuthenticationRequest;
import com.example.shopping.model.AuthenticationResponse;
import com.example.shopping.model.CustomUser;
import com.example.shopping.repository.CustomUserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final CustomUserRepository userRepository;

    public AuthService(CustomUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // רישום משתמש
    public AuthenticationResponse register(AuthenticationRequest request) {
        Optional<CustomUser> existingUser = userRepository.findByEmail(request.getEmail());

        if (existingUser.isPresent()) {
            return new AuthenticationResponse("Email already exists");
        }

        CustomUser newUser = new CustomUser();
        newUser.setFirstName(request.getFirstName());
        newUser.setLastName(request.getLastName());
        newUser.setEmail(request.getEmail());
        newUser.setPhone(request.getPhone());
        newUser.setCountry(request.getCountry());
        newUser.setCity(request.getCity());


        userRepository.save(newUser);

        return new AuthenticationResponse(newUser.getEmail(), "Registration successful");
    }

    // התחברות
    public AuthenticationResponse login(AuthenticationRequest request) {
        Optional<CustomUser> userOptional = userRepository.findByEmail(request.getEmail());

        if (userOptional.isEmpty()) {
            return new AuthenticationResponse("User not found");
        }

        CustomUser user = userOptional.get();


        return new AuthenticationResponse(user.getEmail(), "Login successful");
    }
}


