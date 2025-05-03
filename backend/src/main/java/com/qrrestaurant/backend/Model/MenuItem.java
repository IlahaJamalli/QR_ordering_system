package com.qrrestaurant.backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class MenuItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private double price;
    private String category;
}
