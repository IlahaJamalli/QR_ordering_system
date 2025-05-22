package com.qrrestaurant.backend.controller;

import com.qrrestaurant.backend.model.Order;
import com.qrrestaurant.backend.repository.OrderRepository;
import com.qrrestaurant.backend.messaging.MessageProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class OrderControllerTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private MessageProducer messageProducer;

    @InjectMocks
    private OrderController orderController;

    private Order sampleOrder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Create a sample order for testing
        sampleOrder = new Order();
        sampleOrder.setId("test-order-1");
        sampleOrder.setTableNumber("A1");
        sampleOrder.setStatus("Pending");
        sampleOrder.setOrderedTime(Instant.now());
        
        List<Order.ItemLine> items = new ArrayList<>();
        items.add(new Order.ItemLine("Burger", 2, 15.99));
        sampleOrder.setOrderedItems(items);
        sampleOrder.setTotalPrice(31.98);
    }

    @Test
    void placeOrder_Success() {
        when(orderRepository.save(any(Order.class))).thenReturn(sampleOrder);
        doNothing().when(messageProducer).sendOrderMessage(anyString());

        Order result = orderController.placeOrder(sampleOrder);

        assertNotNull(result);
        assertEquals(sampleOrder.getId(), result.getId());
        assertEquals(sampleOrder.getTableNumber(), result.getTableNumber());
        verify(orderRepository).save(any(Order.class));
        verify(messageProducer).sendOrderMessage(anyString());
    }

    @Test
    void getOrdersByTable_Success() {
        List<Order> expectedOrders = Arrays.asList(sampleOrder);
        when(orderRepository.findByTableNumber("A1")).thenReturn(expectedOrders);

        List<Order> result = orderController.getOrdersByTable("A1");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("A1", result.get(0).getTableNumber());
        verify(orderRepository).findByTableNumber("A1");
    }

    @Test
    void getAllOrders_Success() {
        List<Order> expectedOrders = Arrays.asList(sampleOrder);
        when(orderRepository.findAll()).thenReturn(expectedOrders);

        List<Order> result = orderController.getAllOrders();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(orderRepository).findAll();
    }

    @Test
    void getKitchenOrders_Success() {
        List<Order> expectedOrders = Arrays.asList(sampleOrder);
        when(orderRepository.findByStatusNot("COMPLETED")).thenReturn(expectedOrders);

        List<Order> result = orderController.getKitchenOrders();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(orderRepository).findByStatusNot("COMPLETED");
    }

    @Test
    void getWaiterOrders_Success() {
        List<Order> expectedOrders = Arrays.asList(sampleOrder);
        when(orderRepository.findByStatus("COMPLETED")).thenReturn(expectedOrders);

        List<Order> result = orderController.getWaiterOrders();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(orderRepository).findByStatus("COMPLETED");
    }

    @Test
    void updateOrderStatus_Success() {
        when(orderRepository.findById("test-order-1")).thenReturn(Optional.of(sampleOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(sampleOrder);

        Map<String, String> request = new HashMap<>();
        request.put("status", "PREPARING");

        ResponseEntity<String> response = orderController.updateOrderStatus("test-order-1", request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Status updated to PREPARING", response.getBody());
        verify(orderRepository).findById("test-order-1");
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void updateOrderStatus_NotFound() {
        when(orderRepository.findById("non-existent")).thenReturn(Optional.empty());

        Map<String, String> request = new HashMap<>();
        request.put("status", "PREPARING");

        ResponseEntity<String> response = orderController.updateOrderStatus("non-existent", request);

        assertEquals(404, response.getStatusCodeValue());
        assertEquals("Order not found.", response.getBody());
        verify(orderRepository).findById("non-existent");
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void updateCustomerComment_Success() {
        when(orderRepository.findById("test-order-1")).thenReturn(Optional.of(sampleOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(sampleOrder);

        Map<String, String> request = new HashMap<>();
        request.put("comment", "Test comment");
        request.put("sender", "customer");

        ResponseEntity<String> response = orderController.updateCustomerComment("test-order-1", request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Comment added.", response.getBody());
        verify(orderRepository).findById("test-order-1");
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void deleteOrder_Success() {
        when(orderRepository.existsById("test-order-1")).thenReturn(true);
        doNothing().when(orderRepository).deleteById("test-order-1");

        ResponseEntity<String> response = orderController.deleteOrder("test-order-1");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Order deleted.", response.getBody());
        verify(orderRepository).existsById("test-order-1");
        verify(orderRepository).deleteById("test-order-1");
    }

    @Test
    void deleteOrder_NotFound() {
        when(orderRepository.existsById("non-existent")).thenReturn(false);

        ResponseEntity<String> response = orderController.deleteOrder("non-existent");

        assertEquals(404, response.getStatusCodeValue());
        assertEquals("Order not found.", response.getBody());
        verify(orderRepository).existsById("non-existent");
        verify(orderRepository, never()).deleteById(anyString());
    }
} 