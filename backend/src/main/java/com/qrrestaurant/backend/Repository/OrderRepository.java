package com.qrrestaurant.backend.Repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.qrrestaurant.backend.Model.Order;

public interface OrderRepository extends MongoRepository<Order, String> {
List<Order> findByTableNumber(String tableNumber);

}
