package com.qrrestaurant.backend.model;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.time.Instant;

// Spring Data MongoDB
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.NotNull;

// Lombok
import lombok.Data;

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
    private String status = "Pending";
    private Instant orderedTime = Instant.now();
    private String customerComments = "";

    private List<HistoryLine> history = new ArrayList<>(List.of(new HistoryLine("Pending", orderedTime)));

    private List<Comment> commentsHistory = new ArrayList<>();

    @Data
    public static class ItemLine {
        private String name;
        private int quantity;
        private double price;

        // ✅ New field for customization options
        private List<String> customizations = new ArrayList<>();

        public ItemLine() {} // needed by Spring

        public ItemLine(String name, int quantity, double price) {
            this.name = name;
            this.quantity = quantity;
            this.price = price;
        }

        public ItemLine(String name, int quantity, double price, List<String> customizations) {
            this.name = name;
            this.quantity = quantity;
            this.price = price;
            this.customizations = customizations;
        }
    }

    @Data
    public static class HistoryLine {
        private String status;
        private Instant timestamp;

        public HistoryLine() {}
        public HistoryLine(String status, Instant timestamp) {
            this.status = status;
            this.timestamp = timestamp;
        }
    }

    @Data
    public static class Comment {
        private String sender;
        private String message;
        private Date timestamp;

        public Comment() {}
        public Comment(String sender, String message, Date timestamp) {
            this.sender = sender;
            this.message = message;
            this.timestamp = timestamp;
        }
    }
}
