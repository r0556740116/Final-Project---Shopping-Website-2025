package com.example.shopping.controller;

import com.example.shopping.model.*;
import com.example.shopping.repository.CartRepository;
import com.example.shopping.repository.CustomUserRepository;
import com.example.shopping.repository.ItemRepository;
import com.example.shopping.repository.OrderRepository;
import com.example.shopping.request.CartRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "*")
public class CartController {

    @Autowired
    private CustomUserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private OrderRepository orderRepository;


    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@RequestBody CartRequest request) {
        CustomUser user = userRepository.findByEmail(request.getUserEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        Item item = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        if (item.getStock() <= 0) {
            return ResponseEntity.badRequest().body("The product is out of stock");
        }

        item.setStock(item.getStock() - 1);
        itemRepository.save(item);

        Cart cart = cartRepository.findByUser(user).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUser(user);
            return newCart;
        });

        if (!cart.getItems().contains(item)) {
            cart.getItems().add(item);
        }

        cartRepository.save(cart);

        return ResponseEntity.ok("The product has been successfully added");
    }

    @GetMapping("/{email}")
    public ResponseEntity<?> getCart(@PathVariable String email) {
        CustomUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found"));

        return ResponseEntity.ok(cart);
    }


    @GetMapping
    public List<Item> getCartItems(@RequestParam String userEmail) {
        CustomUser user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        Cart cart = cartRepository.findByUser(user).orElse(new Cart());
        cart.setUser(user);

        return cart.getItems(); // מחזיר את רשימת המוצרים שבעגלה
    }

    @DeleteMapping("/clear")
    public ResponseEntity<?> clearCart(@RequestParam String userEmail) {
        CustomUser user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found"));

        // מחזיר את המלאי חזרה
        cart.getItems().forEach(item -> {
            item.setStock(item.getStock() + 1);
            itemRepository.save(item);
        });

        cart.getItems().clear();
        cartRepository.save(cart);

        return ResponseEntity.ok("The cart has been reset successfully");
    }


    @DeleteMapping("/remove")
    public ResponseEntity<?> removeFromCart(@RequestParam String userEmail, @RequestParam Long itemId) {
        CustomUser user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found"));

        boolean removed = cart.getItems().removeIf(i -> i.getId().equals(itemId));

        if (removed) {
            item.setStock(item.getStock() + 1);
            itemRepository.save(item);
            cartRepository.save(cart);
            return ResponseEntity.ok("The product has been removed from the cart");
        } else {
            return ResponseEntity.badRequest().body("The product is not in the cart");
        }
    }


    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(@RequestParam String userEmail) {
        CustomUser user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found"));

        if (cart.getItems().isEmpty()) {
            return ResponseEntity.badRequest().body("The cart is empty");
        }

        // יצירת הזמנה חדשה ושמירתה
        Order order = new Order();
        order.setUser(user);
        order.setItems(new ArrayList<>(cart.getItems()));
        orderRepository.save(order);

        // ריקון העגלה
        cart.getItems().clear();
        cartRepository.save(cart);

        return ResponseEntity.ok(order);
    }



@PostMapping("/complete")
public ResponseEntity<?> completeOrder(@RequestBody CompleteOrderRequest request) {
    String userEmail = request.getUserEmail();
    String address = request.getAddress();

    System.out.println("USER EMAIL: " + userEmail);
    System.out.println("ADDRESS: " + address);

    CustomUser user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

    Cart cart = cartRepository.findByUser(user)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found"));

    System.out.println("Cart Items Count: " + cart.getItems().size());
    System.out.println("User ID: " + user.getId());

    if (cart.getItems().isEmpty()) {
        return ResponseEntity.badRequest().body("The cart is empty");
    }

    // יצירת הזמנה חדשה עם כתובת ומצב פתוח
    Order order = new Order();
    order.setUser(user);
    order.setItems(new ArrayList<>(cart.getItems()));
    order.setStatus(OrderStatus.COMPLETED);
    order.setAddress(address);

    // חישוב סכום כולל
    double total = cart.getItems().stream()
            .mapToDouble(Item::getPrice)
            .sum();
    order.setTotalPrice(total);

    orderRepository.save(order);

    // ניקוי העגלה
    cart.getItems().clear();
    cartRepository.save(cart);

    return ResponseEntity.ok("Order successfully saved");
}




}





