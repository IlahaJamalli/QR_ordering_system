package com.qrrestaurant.backend.repository;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.qrrestaurant.backend.model.Order;

public interface OrderRepository extends MongoRepository<Order, String> {
    List<Order> findByTableNumber(String tableNumber);
    List<Order> findByStatusNot(String status);   // ✅ must exist
    List<Order> findByStatus(String status);
    List<Order> findByStatusNotIn(List<String> statuses);
}
