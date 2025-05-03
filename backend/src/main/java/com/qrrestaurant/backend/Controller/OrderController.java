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

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:5173")   // adjust for prod
public class OrderController {

    @Autowired
    private OrderRepository orderRepo;

    // ✅ Existing: Get orders by tableNumber (used by TrackOrder.jsx)
    @GetMapping(params = "tableNumber")
    public List<Order> getOrdersByTable(@RequestParam String tableNumber) {
        return orderRepo.findByTableNumber(tableNumber);
    }

    // ✅ Existing: Place new order
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

    // ✅ NEW: Get ALL orders (for KitchenPanel.jsx)
    @GetMapping
    public List<Order> getAllOrders() {
        return orderRepo.findAll();
    }

    // ✅ NEW: Update order status by ID

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
}
