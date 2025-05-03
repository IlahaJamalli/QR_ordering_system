package com.qrrestaurant.backend.Model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection = "orders")
public class Order {

    @Id
    private String id;

    @NotNull
    private String tableNumber;

    @NotNull
    private List<ItemLine> orderedItems = new ArrayList<>();

    private double totalPrice;

    private String status = "Pending";        // Pending ▸ In Preparation ▸ Ready ▸ Delivered
    private Instant orderedTime = Instant.now();
    private String customerComments = "";

    private List<HistoryLine> history = List.of(new HistoryLine("Pending", orderedTime));

    // --- getters & setters (or Lombok @Data) -------------
    // (omitted for brevity)
    // ------------------------------------------------------

    public record ItemLine(String name, int quantity, double price) {}
    public record HistoryLine(String status, Instant timestamp) {}
}
