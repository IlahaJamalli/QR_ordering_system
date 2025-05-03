package com.qrrestaurant.backend.Repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.qrrestaurant.backend.Model.Order;

public interface OrderRepository extends MongoRepository<Order, String> {
}
