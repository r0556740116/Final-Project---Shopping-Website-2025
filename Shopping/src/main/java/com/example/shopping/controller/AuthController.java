
package com.example.shopping.controller;

import com.example.shopping.model.*;
import com.example.shopping.repository.CartRepository;
import com.example.shopping.repository.CustomUserRepository;
import com.example.shopping.repository.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {



    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private OrderRepository orderRepository;

    private final CustomUserRepository userRepository;

    public AuthController(CustomUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest request) {
        Optional<CustomUser> optionalUser = userRepository.findByEmail(request.getEmail());

        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthenticationResponse(null, "Email not found"));
        }

        CustomUser user = optionalUser.get();

        if (!user.getPassword().equals(request.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthenticationResponse(null, "Invalid password"));
        }

        return ResponseEntity.ok(new AuthenticationResponse(user.getEmail(), "Login successful"));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody CustomUser user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AuthenticationResponse(null, "Email already exists"));
        }
        userRepository.save(user);
        return ResponseEntity.ok(new AuthenticationResponse(user.getEmail(), "Registration successful"));
    }
    @Transactional
    @DeleteMapping("/users/delete/{email}")
    public ResponseEntity<String> deleteUser(@PathVariable String email) {
        CustomUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));


        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        try {

            cartRepository.deleteAllByUser(user);
            orderRepository.deleteAllByUser(user);

            userRepository.delete(user);

            return ResponseEntity.ok("User deleted successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting user: " + e.getMessage());
        }
    }


}