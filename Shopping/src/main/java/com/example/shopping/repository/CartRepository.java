package com.example.shopping.repository;

import com.example.shopping.model.Cart;
import com.example.shopping.model.CustomUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    void deleteAllByUser(CustomUser user);
    Optional<Cart> findByUser(CustomUser user);

}
