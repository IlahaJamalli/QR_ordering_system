package com.qrrestaurant.backend.Controller;

import com.qrrestaurant.backend.Model.Order;
import com.qrrestaurant.backend.Repository.OrderRepository;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:5173")
public class OrderController {

    @Autowired
    private OrderRepository orderRepo;

    @GetMapping(params = "tableNumber")
    public List<Order> getOrdersByTable(@RequestParam String tableNumber) {
        return orderRepo.findByTableNumber(tableNumber);
    }

    @PostMapping
    public Order placeOrder(@Valid @RequestBody Order order) {
        if (order.getHistory() == null || order.getHistory().isEmpty()) {
            order.setHistory(
                List.of(new Order.HistoryLine(order.getStatus(), order.getOrderedTime()))
            );
        }
        return orderRepo.save(order);
    }

    @GetMapping
    public List<Order> getAllOrders() {
        return orderRepo.findAll();
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<String> updateOrderStatus(@PathVariable String id, @RequestBody Map<String, String> request) {

        Optional<Order> optionalOrder = orderRepo.findById(id.toString());
        if (optionalOrder.isEmpty()) {
            return ResponseEntity.status(404).body("Order not found.");
        }

        Order order = optionalOrder.get();
        String newStatus = request.get("status");

        order.setStatus(newStatus);
        orderRepo.save(order);

        return ResponseEntity.ok("Status updated to " + newStatus);
    }

    // ✅ Updated to add to commentsHistory, not overwrite
    @PutMapping("/{id}/comment")
    public ResponseEntity<String> updateCustomerComment(@PathVariable String id, @RequestBody Map<String, String> request) {
        Optional<Order> optionalOrder = orderRepo.findById(id);
        if (optionalOrder.isEmpty()) {
            return ResponseEntity.status(404).body("Order not found.");
        }

        Order order = optionalOrder.get();
        String newComment = request.get("comment");
        String sender = request.get("sender"); // "customer" or "kitchen"

        if (order.getCommentsHistory() == null) {
            order.setCommentsHistory(new ArrayList<>());
        }

        order.getCommentsHistory().add(new Order.Comment(sender, newComment, new Date()));
        orderRepo.save(order);

        return ResponseEntity.ok("Comment added.");
    }
}
