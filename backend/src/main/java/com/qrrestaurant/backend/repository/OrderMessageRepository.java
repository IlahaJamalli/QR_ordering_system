package com.qrrestaurant.backend.repository;

import com.qrrestaurant.backend.model.OrderMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderMessageRepository extends JpaRepository<OrderMessage, Long> {

}
