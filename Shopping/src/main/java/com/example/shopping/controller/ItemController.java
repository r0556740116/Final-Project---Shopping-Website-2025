
package com.example.shopping.controller;

import org.springframework.web.bind.annotation.RestController;

import com.example.shopping.model.Item;
import com.example.shopping.repository.ItemRepository;
import org.springframework.web.bind.annotation.*;

        import java.util.List;

@RestController
@RequestMapping("/api/items")
@CrossOrigin(origins = "*")
public class ItemController {

    private final ItemRepository itemRepository;

    public ItemController(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @GetMapping
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    @PostMapping
    public Item addItem(@RequestBody Item item) {
        return itemRepository.save(item);
    }



}
