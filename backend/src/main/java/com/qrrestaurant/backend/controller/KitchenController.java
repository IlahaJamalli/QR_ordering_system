package com.qrrestaurant.backend.controller;

import com.qrrestaurant.backend.model.OrderMessage;
import com.qrrestaurant.backend.repository.OrderMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/kitchen")
@CrossOrigin(origins = "http://localhost:5173")
public class KitchenController {

    @Autowired
    private OrderMessageRepository orderMessageRepository;

    @GetMapping("/messages")
    public List<OrderMessage> getAllMessages() {
        return orderMessageRepository.findAll();
    }
    @PutMapping("/messages/{id}/mark-processed")
    public String markMessageAsProcessed(@PathVariable Long id) {
        return orderMessageRepository.findById(id)
                .map(msg -> {
                    msg.setProcessed(true);
                    orderMessageRepository.save(msg);
                    return "Message ID " + id + " marked as processed.";
                })
                .orElse("Message not found.");
    }

}
