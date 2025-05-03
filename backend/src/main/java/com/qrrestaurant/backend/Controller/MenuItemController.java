package com.qrrestaurant.backend.controller;

import com.qrrestaurant.backend.model.MenuItem;
import com.qrrestaurant.backend.repository.MenuItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menu")
public class MenuItemController {

    @Autowired
    private MenuItemRepository menuItemRepository;

    @GetMapping
    public List<MenuItem> getAllMenuItems() {
        return menuItemRepository.findAll();
    }

    @PostMapping
    public MenuItem createMenuItem(@RequestBody MenuItem item) {
        return menuItemRepository.save(item);
    }
}
