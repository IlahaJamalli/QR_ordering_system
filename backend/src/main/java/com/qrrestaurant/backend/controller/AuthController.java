package com.qrrestaurant.backend.controller;

import com.qrrestaurant.backend.model.Staff;
import com.qrrestaurant.backend.repository.StaffRepository;
import com.qrrestaurant.backend.service.RedisSessionService;
import com.qrrestaurant.backend.service.UserActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final StaffRepository staffRepository;
    private final RedisSessionService redisSessionService;
    private final UserActivityService userActivityService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // Staff Registration
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> request) {
        String name = request.get("name");
        String email = request.get("email");
        String password = request.get("password");
        String role = request.get("role");

        if (staffRepository.findByEmail(email).isPresent()) {
            return ResponseEntity.badRequest().body("Email already registered.");
        }

        Staff staff = Staff.builder()
                .name(name)
                .email(email)
                .password(passwordEncoder.encode(password))
                .role(role)
                .build();

        staffRepository.save(staff);
        return ResponseEntity.ok("Registration successful!");
    }

    // Staff Login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request, @RequestHeader("X-Forwarded-For") String ipAddress) {
        String email = request.get("email");
        String password = request.get("password");

        Staff staff = staffRepository.findByEmail(email)
                .orElse(null);

        if (staff == null) {
            return ResponseEntity.badRequest().body("User not found.");
        }

        if (!passwordEncoder.matches(password, staff.getPassword())) {
            return ResponseEntity.badRequest().body("Incorrect password.");
        }

        // Generate session ID and store in Redis
        String sessionId = UUID.randomUUID().toString();
        redisSessionService.createSession(sessionId, email);
        
        // Record login activity
        userActivityService.recordLogin(email, ipAddress);

        return ResponseEntity.ok(Map.of(
            "message", "Login successful. Welcome, " + staff.getRole(),
            "sessionId", sessionId
        ));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("X-Session-Id") String sessionId) {
        redisSessionService.invalidateSession(sessionId);
        return ResponseEntity.ok("Logged out successfully");
    }

    @GetMapping("/activity")
    public ResponseEntity<?> getUserActivity(@RequestHeader("X-Session-Id") String sessionId) {
        String username = redisSessionService.getUsernameFromSession(sessionId);
        if (username == null) {
            return ResponseEntity.badRequest().body("Invalid session");
        }

        return ResponseEntity.ok(Map.of(
            "loginHistory", userActivityService.getLoginHistory(username),
            "lastActivity", userActivityService.getLastActivityTime(username),
            "activeSessions", redisSessionService.getUserActiveSessions(username)
        ));
    }
}