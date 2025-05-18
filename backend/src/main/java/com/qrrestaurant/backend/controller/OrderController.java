package com.qrrestaurant.backend.controller;

import com.qrrestaurant.backend.model.Order;
import com.qrrestaurant.backend.repository.OrderRepository;
import com.qrrestaurant.backend.messaging.MessageProducer;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ArrayList;
import java.util.Date;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5176"}) // Allow both ports
public class OrderController {

    @Autowired
    private OrderRepository orderRepo;

    @Autowired
    private MessageProducer messageProducer;  // ✅ RabbitMQ producer

    // 🟢 Get orders for a specific table (for customers tracking their own order)
    @GetMapping(params = "tableNumber")
    public List<Order> getOrdersByTable(@RequestParam String tableNumber) {
        return orderRepo.findByTableNumber(tableNumber);
    }

    // 🟢 Place a new order
    @PostMapping
    public Order placeOrder(@Valid @RequestBody Order order) {
        try {
            System.out.println("🚀 Incoming order payload: " + order);

            if (order.getHistory() == null || order.getHistory().isEmpty()) {
                order.setHistory(
                        List.of(new Order.HistoryLine(order.getStatus(), order.getOrderedTime()))
                );
            }

            Order savedOrder = orderRepo.save(order);

            String message = "New order placed: Table " + order.getTableNumber() + ", Order ID: " + savedOrder.getId();
            messageProducer.sendOrderMessage(message);

            return savedOrder;
        } catch (Exception e) {
            System.err.println("❌ Error while saving order:");
            e.printStackTrace(); // <-- This will print the exact cause in the terminal
            throw e;
        }
    }


    // 🟢 Get ALL orders (for manager)
    @GetMapping
    public List<Order> getAllOrders() {
        return orderRepo.findAll();
    }

    // 🟢 Get orders for kitchen (NOT completed)
    @GetMapping("/kitchen")
    public List<Order> getKitchenOrders() {
        return orderRepo.findByStatusNot("COMPLETED");
    }

    // 🟢 Get orders for waiter (only COMPLETED)
    @GetMapping("/waiter")
    public List<Order> getWaiterOrders() {
        List<Order> completed = orderRepo.findByStatus("COMPLETED");
        System.out.println("👀 Completed Orders: " + completed.size());
        return completed;
    }


    // 🟢 Update order status (Kitchen, Waiter)
    @PutMapping("/{id}/status")
    public ResponseEntity<String> updateOrderStatus(@PathVariable String id, @RequestBody Map<String, String> request) {
        Optional<Order> optionalOrder = orderRepo.findById(id);
        if (optionalOrder.isEmpty()) {
            return ResponseEntity.status(404).body("Order not found.");
        }

        Order order = optionalOrder.get();
        String newStatus = request.get("status");

        order.setStatus(newStatus);

        // Add to status history
        if (order.getHistory() == null) {
            order.setHistory(new ArrayList<>());
        }
        order.getHistory().add(new Order.HistoryLine(newStatus, Instant.now()));

        orderRepo.save(order);

        return ResponseEntity.ok("Status updated to " + newStatus);
    }

    // 🟢 Add a comment (chat from kitchen or customer)
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

    // 🟠 Manual Delete (Only if Admin/Manager wants — NOT automatic)
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOrder(@PathVariable String id) {
        if (!orderRepo.existsById(id)) {
            return ResponseEntity.status(404).body("Order not found.");
        }

        orderRepo.deleteById(id);
        return ResponseEntity.ok("Order deleted.");
    }
}
