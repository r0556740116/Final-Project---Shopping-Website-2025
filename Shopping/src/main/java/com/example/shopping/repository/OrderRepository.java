//package com.example.shopping.repository;
//
//import com.example.shopping.model.Order;
//import com.example.shopping.model.CustomUser;
//import com.example.shopping.model.OrderStatus;
//import org.springframework.data.jpa.repository.JpaRepository;
//import java.util.List;
//import java.util.Optional;
//
//
//
//
//public interface OrderRepository extends JpaRepository<Order, Long> {
//    List<Order> findByUser(CustomUser user);
//
//    Optional<Order> findByUserAndStatus(CustomUser user, OrderStatus status);
//    List<Order> findByUserEmail(String email);
//
//
//}
package com.example.shopping.repository;

import com.example.shopping.model.Order;
import com.example.shopping.model.CustomUser;
import com.example.shopping.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // מחיקה של כל ההזמנות לפי משתמש
    void deleteAllByUser(CustomUser user);

    // כל ההזמנות של משתמש
    List<Order> findByUser(CustomUser user);

    // להזמנה פתוחה לפי משתמש
    Optional<Order> findByUserAndStatus(CustomUser user, OrderStatus status);

    // חיפוש לפי אימייל של המשתמש
    List<Order> findByUserEmail(String email);
}
