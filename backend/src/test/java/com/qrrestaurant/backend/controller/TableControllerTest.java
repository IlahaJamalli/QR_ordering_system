package com.qrrestaurant.backend.controller;

import com.qrrestaurant.backend.model.TableEntity;
import com.qrrestaurant.backend.repository.TableRepository;
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

class TableControllerTest {

    @Mock
    private TableRepository tableRepository;

    @InjectMocks
    private TableController tableController;

    private TableEntity sampleTable;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Create a sample table for testing
        sampleTable = new TableEntity();
        sampleTable.setId(1L);
        sampleTable.setTableNumber("A1");
        sampleTable.setQrCode("/qrcodes/table_A1.png");
    }

    @Test
    void getAllTables_Success() {
        List<TableEntity> expectedTables = Arrays.asList(sampleTable);
        when(tableRepository.findAll()).thenReturn(expectedTables);

        List<TableEntity> result = tableController.getAllTables();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(sampleTable.getTableNumber(), result.get(0).getTableNumber());
        verify(tableRepository).findAll();
    }

    @Test
    void getTableById_Success() {
        when(tableRepository.findById(1L)).thenReturn(Optional.of(sampleTable));

        TableEntity result = tableController.getTableById(1L);

        assertNotNull(result);
        assertEquals(sampleTable.getId(), result.getId());
        assertEquals(sampleTable.getTableNumber(), result.getTableNumber());
        verify(tableRepository).findById(1L);
    }

    @Test
    void getTableById_NotFound() {
        when(tableRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            tableController.getTableById(999L);
        });

        verify(tableRepository).findById(999L);
    }
}