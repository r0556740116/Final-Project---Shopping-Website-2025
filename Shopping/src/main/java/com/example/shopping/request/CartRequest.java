package com.example.shopping.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartRequest {
    private String userEmail;
    private Long itemId;
}



