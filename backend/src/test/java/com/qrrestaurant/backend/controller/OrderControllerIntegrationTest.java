package com.qrrestaurant.backend.controller;

import com.qrrestaurant.backend.model.Order;
import com.qrrestaurant.backend.repository.OrderRepository;
import com.qrrestaurant.backend.messaging.MessageProducer;
import com.qrrestaurant.backend.config.TestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ArrayList;
import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestConfig.class)
public class OrderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderRepository orderRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MessageProducer messageProducer;

    private Order testOrder;
    private List<Order> testOrders;

    @BeforeEach
    void setUp() {
        reset(orderRepository, messageProducer);

        // Setup test order
        testOrder = new Order();
        testOrder.setId("1");
        testOrder.setTableNumber("1");
        testOrder.setStatus("PENDING");
        testOrder.setOrderedTime(Instant.now());

        // Setup test orders list
        testOrders = new ArrayList<>();
        testOrders.add(testOrder);

        // Configure mock behavior
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
        when(orderRepository.findById("1")).thenReturn(Optional.of(testOrder));
        when(orderRepository.findByTableNumber("1")).thenReturn(testOrders);
        when(orderRepository.findByStatusNot("COMPLETED")).thenReturn(testOrders);
        when(orderRepository.findByStatus("COMPLETED")).thenReturn(testOrders);
        doNothing().when(messageProducer).sendOrderMessage(anyString());
    }

    @Test
    void placeOrder_ShouldCreateNewOrder_WithValidData() throws Exception {
        Order order = new Order();
        order.setTableNumber("1");
        order.setStatus("PENDING");
        order.setOrderedTime(Instant.now());

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.tableNumber").value("1"))
                .andExpect(jsonPath("$.status").value("PENDING"));

        verify(messageProducer, times(1)).sendOrderMessage(anyString());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void getOrdersByTable_ShouldReturnOrders_WhenTableHasOrders() throws Exception {
        mockMvc.perform(get("/api/orders")
                .param("tableNumber", "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].tableNumber").value("1"));

        verify(orderRepository, times(1)).findByTableNumber("1");
    }

    @Test
    void updateOrderStatus_ShouldUpdateStatus_WhenOrderExists() throws Exception {
        Map<String, String> statusUpdate = Map.of("status", "PREPARING");

        // Configure mock for updated order
        Order updatedOrder = new Order();
        updatedOrder.setId("1");
        updatedOrder.setTableNumber("1");
        updatedOrder.setStatus("PREPARING");
        updatedOrder.setOrderedTime(Instant.now());
        when(orderRepository.save(any(Order.class))).thenReturn(updatedOrder);

        mockMvc.perform(put("/api/orders/{id}/status", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(statusUpdate)))
                .andExpect(status().isOk())
                .andExpect(content().string("Status updated to PREPARING"));

        verify(orderRepository, times(1)).findById("1");
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void addComment_ShouldAddComment_WhenOrderExists() throws Exception {
        Map<String, String> comment = Map.of(
                "comment", "Test comment",
                "sender", "customer");

        // Configure mock for order with comment
        Order orderWithComment = new Order();
        orderWithComment.setId("1");
        orderWithComment.setTableNumber("1");
        orderWithComment.setStatus("PENDING");
        orderWithComment.setOrderedTime(Instant.now());
        orderWithComment.setCommentsHistory(List.of(new Order.Comment("Test comment", "customer", new Date())));
        when(orderRepository.save(any(Order.class))).thenReturn(orderWithComment);

        mockMvc.perform(put("/api/orders/{id}/comment", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(comment)))
                .andExpect(status().isOk())
                .andExpect(content().string("Comment added."));

        verify(orderRepository, times(1)).findById("1");
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void getKitchenOrders_ShouldReturnNonCompletedOrders() throws Exception {
        mockMvc.perform(get("/api/orders/kitchen"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].status").value("PENDING"));

        verify(orderRepository, times(1)).findByStatusNot("COMPLETED");
    }

    @Test
    void getWaiterOrders_ShouldReturnCompletedOrders() throws Exception {
        mockMvc.perform(get("/api/orders/waiter"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].status").value("PENDING"));

        verify(orderRepository, times(1)).findByStatus("COMPLETED");
    }

}