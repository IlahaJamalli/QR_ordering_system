package com.qrrestaurant.backend.controller;

import com.qrrestaurant.backend.model.TableEntity;
import com.qrrestaurant.backend.repository.TableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tables")
public class TableController {

    @Autowired
    private TableRepository tableRepository;

    @GetMapping
    public List<TableEntity> getAllTables() {
        return tableRepository.findAll();
    }

    @GetMapping("/{id}")
    public TableEntity getTableById(@PathVariable Long id) {
        return tableRepository.findById(id).orElse(null);
    }

    @PostMapping
    public TableEntity createTable(@RequestBody TableEntity table) {
        try {
            // Construct the URL that the QR should point to
            String url = "http://localhost:5173/order?tableId=" + table.getTableNumber();
//FROM PHONE UNCOMMENT
            //yourcomp ip
            //ipconfig smth with 192.168.x.x
            //String url = "http://<your_computer_ip>:5173/order?tableId=" + table.getTableNumber();
            // Generate QR Code image
            String qrCodePath = com.qrrestaurant.backend.util.QrCodeGenerator.generateQRCodeImage(url, "table_" + table.getTableNumber());

            // Save the path in the database
            table.setQrCodeUrl(qrCodePath);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return tableRepository.save(table);
    }

}
