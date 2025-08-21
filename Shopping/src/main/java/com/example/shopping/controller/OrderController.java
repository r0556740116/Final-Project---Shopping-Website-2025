package com.example.shopping.controller;

import com.example.shopping.model.CompleteOrderRequest;
import com.example.shopping.model.CustomUser;
import com.example.shopping.model.Order;
import com.example.shopping.model.OrderStatus;
import com.example.shopping.model.Item;
import com.example.shopping.repository.CustomUserRepository;
import com.example.shopping.repository.OrderRepository;
import com.example.shopping.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    @Autowired
    private CustomUserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ItemRepository itemRepository;


    // נקודת קצה לשליפת כל ההזמנות של המשתמש (כולל הזמנות פתוחות)
    @GetMapping("/my-orders")
    public ResponseEntity<List<Order>> getMyOrders(@RequestParam String userEmail) {
        CustomUser user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        List<Order> orders = orderRepository.findByUser(user);
        return ResponseEntity.ok(orders);
    }

    // מחיקת מוצר מההזמנה הפתוחה (עגלה)
    @DeleteMapping("/open-order/remove-item")
    public ResponseEntity<?> removeItemFromOpenOrder(
            @RequestParam String userEmail,
            @RequestParam Long itemId) {

        CustomUser user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Order openOrder = orderRepository.findByUserAndStatus(user, OrderStatus.TEMP)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No open order found"));

        boolean removed = openOrder.getItems().removeIf(item -> item.getId().equals(itemId));
        if (!removed) {
            return ResponseEntity.badRequest().body("The product is not on order");
        }

        orderRepository.save(openOrder);

        return ResponseEntity.ok("The product has been removed from the order");
    }

    // הוספת מוצר להזמנה פתוחה (סטטוס TEMP)
    @PostMapping("/add-item")
    public ResponseEntity<?> addItemToOrder(@RequestParam String userEmail, @RequestParam Long itemId) {
        CustomUser user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found"));

        Optional<Order> openOrderOpt = orderRepository.findByUserAndStatus(user, OrderStatus.TEMP);

        Order order;
        if (openOrderOpt.isPresent()) {
            order = openOrderOpt.get();
        } else {
            order = new Order();
            order.setUser(user);
            order.setStatus(OrderStatus.TEMP);
            order.setItems(new ArrayList<>());
        }

        order.getItems().add(item);

        double total = order.getItems().stream().mapToDouble(Item::getPrice).sum();
        order.setTotalPrice(total);

        orderRepository.save(order);

        return ResponseEntity.ok(order);
    }

    // קבלת הזמנה פתוחה
    @GetMapping("/open-order")
    public ResponseEntity<Order> getOpenOrder(@RequestParam String userEmail) {
        CustomUser user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Order openOrder = orderRepository.findByUserAndStatus(user, OrderStatus.TEMP)
                .orElse(null);

        if (openOrder == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(openOrder);
    }

    // השלמת הזמנה פתוחה והפיכתה ל-COMPLETED
    @PostMapping("/complete")
    public ResponseEntity<?> completeOrder(@RequestParam String userEmail, @RequestBody CompleteOrderRequest request) {
        CustomUser user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Optional<Order> openOrderOpt = orderRepository.findByUserAndStatus(user, OrderStatus.TEMP);
        if (openOrderOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("There is no open invitation to finish");
        }
        Order order = openOrderOpt.get();

        order.setStatus(OrderStatus.COMPLETED);
        order.setAddress(request.getAddress());
        order.setOrderDate(LocalDateTime.now());

        orderRepository.save(order);

        return ResponseEntity.ok("The order was successfully completed");
    }
}
