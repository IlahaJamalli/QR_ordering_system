package com.qrrestaurant.backend.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
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

    private String status = "Pending";
    private Instant orderedTime = Instant.now();
    private String customerComments = "";

    private List<HistoryLine> history = new ArrayList<>(List.of(new HistoryLine("Pending", orderedTime)));

    // ✅ New: Chat comment history
    private List<Comment> commentsHistory = new ArrayList<>();

    @Data
    public static class ItemLine {
        private String name;
        private int quantity;
        private double price;

        public ItemLine() {} // needed by Spring
        public ItemLine(String name, int quantity, double price) {
            this.name = name;
            this.quantity = quantity;
            this.price = price;
        }
    }

    @Data
    public static class HistoryLine {
        private String status;
        private Instant timestamp;

        public HistoryLine() {} // needed by Spring
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

        public Comment() {} // needed by Spring
        public Comment(String sender, String message, Date timestamp) {
            this.sender = sender;
            this.message = message;
            this.timestamp = timestamp;
        }
    }
}
