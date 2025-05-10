package com.qrrestaurant.backend.Controller;

import com.qrrestaurant.backend.model.Staff;
import com.qrrestaurant.backend.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final StaffRepository staffRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // Staff Registration
    @PostMapping("/register")
    public String register(@RequestBody Map<String, String> request) {
        String name = request.get("name");
        String email = request.get("email");
        String password = request.get("password");
        String role = request.get("role");

        if (staffRepository.findByEmail(email).isPresent()) {
            return "Email already registered.";
        }

        Staff staff = Staff.builder()
                .name(name)
                .email(email)
                .password(passwordEncoder.encode(password))
                .role(role)
                .build();

        staffRepository.save(staff);
        return "Registration successful!";
    }

    // Staff Login
    @PostMapping("/login")
    public String login(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");

        Staff staff = staffRepository.findByEmail(email)
                .orElse(null);

        if (staff == null) {
            return "User not found.";
        }

        if (!passwordEncoder.matches(password, staff.getPassword())) {
            return "Incorrect password.";
        }

        return "Login successful. Welcome, " + staff.getRole();
    }
}