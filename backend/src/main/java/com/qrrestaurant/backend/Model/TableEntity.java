package com.qrrestaurant.backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "restaurant_table")  // PostgreSQL doesn't like 'Table' as table name
public class TableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String tableNumber;

    private String qrCodeUrl;
}
