package com.qrrestaurant.backend.Controller;

import com.qrrestaurant.backend.Model.Order;
import com.qrrestaurant.backend.Repository.OrderRepository;
import jakarta.validation.Valid;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:5173")   // adjust for prod
public class OrderController {

    @Autowired
    private OrderRepository orderRepo;


    @GetMapping
    public List<Order> getOrdersByTable(@RequestParam String tableNumber) {
        return orderRepo.findByTableNumber(tableNumber);
}
    @PostMapping
    public Order placeOrder(@Valid @RequestBody Order order) {
        // ensure initial history entry exists
        if (order.getHistory() == null || order.getHistory().isEmpty()) {
            order.setHistory(
                List.of(new Order.HistoryLine(order.getStatus(), order.getOrderedTime()))
            );
        }
        return orderRepo.save(order);
    }
}
