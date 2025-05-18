package com.qrrestaurant.backend.controller;

import com.qrrestaurant.backend.model.Staff;
import com.qrrestaurant.backend.repository.StaffRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class AuthControllerTest {

    @Mock
    private StaffRepository staffRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthController authController;

    private Staff sampleStaff;
    private Map<String, String> validRegisterRequest;
    private Map<String, String> validLoginRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Create a sample staff member for testing
        sampleStaff = Staff.builder()
                .id(1L)
                .name("Test Staff")
                .email("test@example.com")
                .password("encodedPassword")
                .role("waiter")
                .build();

        // Create valid register request
        validRegisterRequest = new HashMap<>();
        validRegisterRequest.put("name", "Test Staff");
        validRegisterRequest.put("email", "test@example.com");
        validRegisterRequest.put("password", "password123");
        validRegisterRequest.put("role", "waiter");

        // Create valid login request
        validLoginRequest = new HashMap<>();
        validLoginRequest.put("email", "test@example.com");
        validLoginRequest.put("password", "password123");
    }

    @Test
    void register_Success() {
        when(staffRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(staffRepository.save(any(Staff.class))).thenReturn(sampleStaff);

        String result = authController.register(validRegisterRequest);

        assertEquals("Registration successful!", result);
        verify(staffRepository).findByEmail(validRegisterRequest.get("email"));
        verify(staffRepository).save(any(Staff.class));
    }

    @Test
    void register_EmailAlreadyExists() {
        when(staffRepository.findByEmail(anyString())).thenReturn(Optional.of(sampleStaff));

        String result = authController.register(validRegisterRequest);

        assertEquals("Email already registered.", result);
        verify(staffRepository).findByEmail(validRegisterRequest.get("email"));
        verify(staffRepository, never()).save(any(Staff.class));
    }

    @Test
    void login_UserNotFound() {
        when(staffRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        String result = authController.login(validLoginRequest);

        assertEquals("User not found.", result);
        verify(staffRepository).findByEmail(validLoginRequest.get("email"));
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }
}