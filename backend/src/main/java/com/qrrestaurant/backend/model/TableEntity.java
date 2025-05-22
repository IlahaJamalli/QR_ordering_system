package com.qrrestaurant.backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "restaurant_table")  // Avoid using reserved keyword "Table"
public class TableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String tableNumber;

    private int capacity;

    private String qrCode;

    // Explicit setters and getters (optional if using @Data, but included for clarity)
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public void setTableNumber(String tableNumber) {
        this.tableNumber = tableNumber;
    }

    public Long getId() {
        return id;
    }

    public String getTableNumber() {
        return tableNumber;
    }

    public String getQrCode() {
        return qrCode;
    }

    public int getCapacity() {
        return capacity;
    }
}
