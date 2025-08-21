
package com.example.shopping.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class CustomUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;

    @Column(unique = true)
    private String email;
    private String phone;
    private String country;
    private String city;
    private String password;

    public void setLastName(String lastName) {
    }
}

