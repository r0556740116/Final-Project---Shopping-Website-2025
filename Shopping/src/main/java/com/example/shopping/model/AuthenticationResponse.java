package com.example.shopping.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponse {
    private String email;
    private String message;

    public AuthenticationResponse(String emailAlreadyExists) {
    }
    }



