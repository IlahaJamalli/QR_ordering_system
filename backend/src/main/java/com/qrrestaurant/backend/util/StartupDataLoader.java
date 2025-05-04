package com.qrrestaurant.backend.util;

import com.qrrestaurant.backend.Model.Staff;
import com.qrrestaurant.backend.Repository.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class StartupDataLoader implements CommandLineRunner {

    @Autowired
    private StaffRepository staffRepo;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        staffRepo.deleteAll();
        createStaffIfNotExists("kitchen@example.com", "1234", "kitchen_staff");
        createStaffIfNotExists("waiter@example.com", "1234", "waiter");
        createStaffIfNotExists("manager@example.com", "1234", "manager");
    }

    private void createStaffIfNotExists(String email, String rawPassword, String role) {
        if (!staffRepo.existsByEmail(email)) {
            Staff staff = new Staff();
            staff.setEmail(email);
            staff.setPassword(passwordEncoder.encode(rawPassword));
            staff.setRole(role);
            staffRepo.save(staff);
            System.out.println("Created default staff: " + email + " / role: " + role);
        }
    }
}
