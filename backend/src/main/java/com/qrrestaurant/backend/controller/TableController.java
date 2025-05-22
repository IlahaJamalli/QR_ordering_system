package com.qrrestaurant.backend.controller;

import com.qrrestaurant.backend.model.TableEntity;
import com.qrrestaurant.backend.repository.TableRepository;
import com.qrrestaurant.backend.util.QrCodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tables")
public class TableController {

    @Autowired
    private TableRepository tableRepository;

    @Autowired
    private QrCodeGenerator qrCodeGenerator;

    // ✅ Get all tables — no exception handling needed
    @GetMapping
    public List<TableEntity> getAllTables() {
        return tableRepository.findAll();
    }

    // ✅ Get table by ID — improved exception handling
    @GetMapping("/{id}")
    public TableEntity getTableById(@PathVariable Long id) {
        return tableRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Table not found"));
    }

    // ✅ Create new table with QR code generation
    @PostMapping
    public TableEntity createTable(@RequestBody TableEntity table) {
        try {
            // Construct the URL that the QR should point to
            String url = "http://localhost:5173/menu?tableId=" + table.getTableNumber();
//FROM PHONE UNCOMMENT
            //yourcomp ip
            //ipconfig smth with 192.168.x.x
//            String url = "http://192.168.0.105:5173/order?tableId=" + table.getTableNumber();
            // Generate QR Code image
            String qrCodePath = qrCodeGenerator.generateQRCodeImage(url, "table_" + table.getTableNumber());

            // Save the path in the database
            table.setQrCode(qrCodePath);

        } catch (Exception e) {
            // ✅ Better to throw the exception so GlobalExceptionHandler catches it
            throw new RuntimeException("Failed to generate QR code: " + e.getMessage());
        }

        return tableRepository.save(table);
    }
}
