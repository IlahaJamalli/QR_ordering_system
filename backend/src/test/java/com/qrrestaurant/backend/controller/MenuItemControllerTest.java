package com.qrrestaurant.backend.controller;

import com.qrrestaurant.backend.model.MenuItem;
import com.qrrestaurant.backend.repository.MenuItemRepository;
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

class MenuItemControllerTest {

    @Mock
    private MenuItemRepository menuItemRepository;

    @InjectMocks
    private MenuItemController menuItemController;

    private MenuItem sampleMenuItem;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Create a sample menu item for testing
        sampleMenuItem = new MenuItem();
        sampleMenuItem.setId(1L);
        sampleMenuItem.setName("Test Burger");
        sampleMenuItem.setDescription("A delicious test burger");
        sampleMenuItem.setPrice(9.99);
        sampleMenuItem.setCategory("Burgers");
    }

    @Test
    void getAllMenuItems_Success() {
        List<MenuItem> expectedItems = Arrays.asList(sampleMenuItem);
        when(menuItemRepository.findAll()).thenReturn(expectedItems);

        List<MenuItem> result = menuItemController.getAllMenuItems();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(sampleMenuItem.getName(), result.get(0).getName());
        verify(menuItemRepository).findAll();
    }

    @Test
    void createMenuItem_Success() {
        when(menuItemRepository.save(any(MenuItem.class))).thenReturn(sampleMenuItem);

        MenuItem result = menuItemController.createMenuItem(sampleMenuItem);

        assertNotNull(result);
        assertEquals(sampleMenuItem.getName(), result.getName());
        assertEquals(sampleMenuItem.getPrice(), result.getPrice());
        verify(menuItemRepository).save(any(MenuItem.class));
    }

    @Test
    void getMenuItemById_Success() {
        when(menuItemRepository.findById(1L)).thenReturn(Optional.of(sampleMenuItem));

        MenuItem result = menuItemController.getMenuItemById(1L);

        assertNotNull(result);
        assertEquals(sampleMenuItem.getId(), result.getId());
        assertEquals(sampleMenuItem.getName(), result.getName());
        verify(menuItemRepository).findById(1L);
    }

    @Test
    void getMenuItemById_NotFound() {
        when(menuItemRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            menuItemController.getMenuItemById(999L);
        });

        verify(menuItemRepository).findById(999L);
    }
} 