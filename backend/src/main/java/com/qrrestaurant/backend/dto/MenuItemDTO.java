package com.qrrestaurant.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor  // ✅ Automatically creates a no-arg constructor
public class MenuItemDTO {
    private Long id;
    private String name;
    private String description;
    private double price;
    private String category;
    private List<String> customizationOptions;
}
