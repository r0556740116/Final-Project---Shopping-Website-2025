package com.example.shopping.controller;

import com.example.shopping.model.*;
import com.example.shopping.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/favorites")
@CrossOrigin(origins = "*")
public class FavoriteController {

    @Autowired
    private FavoriteRepository favoriteRepo;

    @Autowired
    private CustomUserRepository userRepo;

    @Autowired
    private ItemRepository itemRepo;


    @GetMapping
    public List<Favorite> getFavorites(Principal principal) {
        CustomUser user = userRepo.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return favoriteRepo.findByUser(user);
    }

    // הוספה למועדפים
    @PostMapping("/{itemId}")
    public ResponseEntity<?> addFavorite(@PathVariable Long itemId, Principal principal) {
        CustomUser user = userRepo.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Item item = itemRepo.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        if (favoriteRepo.findByUserAndItem(user, item).isPresent()) {
            return ResponseEntity.badRequest().body("Already in favorites");
        }

        favoriteRepo.save(new Favorite(user, item));
        return ResponseEntity.ok().build();
    }

    // הסרה ממועדפים
    @DeleteMapping("/{itemId}")
    public ResponseEntity<?> removeFavorite(@PathVariable Long itemId, Principal principal) {
        CustomUser user = userRepo.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Item item = itemRepo.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        favoriteRepo.findByUserAndItem(user, item).ifPresent(fav -> favoriteRepo.delete(fav));
        return ResponseEntity.ok().build();
    }
}
