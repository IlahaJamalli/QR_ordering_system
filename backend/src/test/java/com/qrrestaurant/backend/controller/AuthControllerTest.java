package com.qrrestaurant.backend.controller;

import com.qrrestaurant.backend.model.Staff;
import com.qrrestaurant.backend.repository.StaffRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.fail;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

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

        sampleStaff = Staff.builder()
                .id(1L)
                .name("Test Staff")
                .email("test@example.com")
                .password("encodedPassword")
                .role("waiter")
                .build();

        validRegisterRequest = new HashMap<>();
        validRegisterRequest.put("name", "Test Staff");
        validRegisterRequest.put("email", "test@example.com");
        validRegisterRequest.put("password", "password123");
        validRegisterRequest.put("role", "waiter");

        validLoginRequest = new HashMap<>();
        validLoginRequest.put("email", "test@example.com");
        validLoginRequest.put("password", "password123");
    }

    @Test
    void register_Success() {
        when(staffRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(staffRepository.save(any(Staff.class))).thenReturn(sampleStaff);

        ResponseEntity<?> response = authController.register(validRegisterRequest);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Registration successful!", response.getBody());

        verify(staffRepository).findByEmail(validRegisterRequest.get("email"));
        verify(staffRepository).save(any(Staff.class));
    }

    @Test
    void register_EmailAlreadyExists() {
        when(staffRepository.findByEmail(anyString())).thenReturn(Optional.of(sampleStaff));

        ResponseEntity<?> response = authController.register(validRegisterRequest);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Email already registered.", response.getBody());

        verify(staffRepository).findByEmail(validRegisterRequest.get("email"));
        verify(staffRepository, never()).save(any(Staff.class));
    }

    @Test
    void login_UserNotFound() {
        when(staffRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        ResponseEntity<?> response = authController.login(validLoginRequest, "waiter");

        assertEquals(404, response.getStatusCodeValue());

        Object responseBody = response.getBody();

        // If controller returns: ResponseEntity.status(404).body(Map.of("message", "User not found."))
        if (responseBody instanceof Map<?, ?> map) {
            assertEquals("User not found.", map.get("message"));
        } else {
            fail("Response body is not a map as expected");
        }

        verify(staffRepository).findByEmail(validLoginRequest.get("email"));
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }
}
