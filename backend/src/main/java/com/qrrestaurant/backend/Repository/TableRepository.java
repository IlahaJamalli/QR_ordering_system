package com.qrrestaurant.backend.repository;

import com.qrrestaurant.backend.model.TableEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TableRepository extends JpaRepository<TableEntity, Long> {
}
