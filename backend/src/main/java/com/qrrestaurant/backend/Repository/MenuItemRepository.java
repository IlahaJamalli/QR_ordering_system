package com.qrrestaurant.backend.repository;

import com.qrrestaurant.backend.model.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
}
