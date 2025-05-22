package com.qrrestaurant.backend.controller;

import com.qrrestaurant.backend.model.OrderMessage;
import com.qrrestaurant.backend.repository.OrderMessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class KitchenControllerTest {

    @Mock
    private OrderMessageRepository orderMessageRepository;

    @InjectMocks
    private KitchenController kitchenController;

    private OrderMessage sampleMessage;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Create a sample message for testing
        sampleMessage = new OrderMessage();
        sampleMessage.setId(1L);
        sampleMessage.setContent("New order for table A1");
        sampleMessage.setProcessed(false);
    }

    @Test
    void getAllMessages_Success() {
        List<OrderMessage> expectedMessages = Arrays.asList(sampleMessage);
        when(orderMessageRepository.findAll()).thenReturn(expectedMessages);

        List<OrderMessage> result = kitchenController.getAllMessages();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(sampleMessage.getContent(), result.get(0).getContent());
        verify(orderMessageRepository).findAll();
    }

    @Test
    void markMessageAsProcessed_Success() {
        when(orderMessageRepository.findById(1L)).thenReturn(Optional.of(sampleMessage));
        when(orderMessageRepository.save(any(OrderMessage.class))).thenReturn(sampleMessage);

        String result = kitchenController.markMessageAsProcessed(1L);

        assertEquals("Message ID 1 marked as processed.", result);
        assertTrue(sampleMessage.isProcessed());
        verify(orderMessageRepository).findById(1L);
        verify(orderMessageRepository).save(any(OrderMessage.class));
    }

    @Test
    void markMessageAsProcessed_NotFound() {
        when(orderMessageRepository.findById(999L)).thenReturn(Optional.empty());

        String result = kitchenController.markMessageAsProcessed(999L);

        assertEquals("Message not found.", result);
        verify(orderMessageRepository).findById(999L);
        verify(orderMessageRepository, never()).save(any(OrderMessage.class));
    }
} 