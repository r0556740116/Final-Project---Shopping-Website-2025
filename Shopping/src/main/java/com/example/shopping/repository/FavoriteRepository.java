package com.example.shopping.repository;

import com.example.shopping.model.Favorite;
import com.example.shopping.model.CustomUser;
import com.example.shopping.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findByUser(CustomUser user);
    Optional<Favorite> findByUserAndItem(CustomUser user, Item item);
}
