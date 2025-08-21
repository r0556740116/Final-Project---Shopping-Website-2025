package com.example.shopping.model;

import jakarta.persistence.*;

@Entity
public class Favorite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private CustomUser user;


    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    public Favorite() {}
    public Favorite(CustomUser user, Item item) {
        this.user = user;
        this.item = item;
    }

    public Long getId() { return id; }
    public CustomUser getUser() { return user; }
    public Item getItem() { return item; }
}
