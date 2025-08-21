package com.example.shopping.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "השרת פעיל, ברוכה הבאה לאפליקציית הקניות.";
    }
}

