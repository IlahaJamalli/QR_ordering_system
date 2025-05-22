package com.qrrestaurant.backend.util;

import com.qrrestaurant.backend.model.Staff;
import com.qrrestaurant.backend.repository.StaffRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class StartupDataLoaderTest {

    @Mock
    private StaffRepository staffRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private StartupDataLoader startupDataLoader;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
    }

    @Test
    void run_CreatesDefaultStaff() throws Exception {
        // Mock repository behavior
        when(staffRepository.existsByEmail(anyString())).thenReturn(false);
        when(staffRepository.save(any(Staff.class))).thenAnswer(i -> i.getArgument(0));

        // Execute the run method
        startupDataLoader.run();

        // Verify that deleteAll was called
        verify(staffRepository).deleteAll();

        // Verify that save was called for each default staff member
        verify(staffRepository, times(3)).save(any(Staff.class));
    }

    @Test
    void run_SkipsExistingStaff() throws Exception {
        // Mock repository behavior to simulate existing staff
        when(staffRepository.existsByEmail(anyString())).thenReturn(true);

        // Execute the run method
        startupDataLoader.run();

        // Verify that deleteAll was called
        verify(staffRepository).deleteAll();

        // Verify that save was never called since all staff exist
        verify(staffRepository, never()).save(any(Staff.class));
    }

    @Test
    void createStaffIfNotExists_NewStaff() {
        // Mock repository behavior
        when(staffRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(staffRepository.save(any(Staff.class))).thenAnswer(i -> i.getArgument(0));

        // Create staff using reflection to access private method
        try {
            java.lang.reflect.Method method = StartupDataLoader.class.getDeclaredMethod(
                "createStaffIfNotExists", String.class, String.class, String.class);
            method.setAccessible(true);
            method.invoke(startupDataLoader, "test@example.com", "password123", "waiter");
        } catch (Exception e) {
            fail("Failed to invoke private method", e);
        }

        // Verify that save was called
        verify(staffRepository).save(any(Staff.class));
    }

    @Test
    void createStaffIfNotExists_ExistingStaff() {
        // Mock repository behavior
        when(staffRepository.existsByEmail("test@example.com")).thenReturn(true);

        // Create staff using reflection to access private method
        try {
            java.lang.reflect.Method method = StartupDataLoader.class.getDeclaredMethod(
                "createStaffIfNotExists", String.class, String.class, String.class);
            method.setAccessible(true);
            method.invoke(startupDataLoader, "test@example.com", "password123", "waiter");
        } catch (Exception e) {
            fail("Failed to invoke private method", e);
        }

        // Verify that save was never called
        verify(staffRepository, never()).save(any(Staff.class));
    }
} 